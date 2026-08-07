package com.sandrojam.controlevendas.service;

import com.sandrojam.controlevendas.dto.ClienteDevedorDTO;
import com.sandrojam.controlevendas.dto.ContasAPagarResumoDTO;
import com.sandrojam.controlevendas.dto.ContasAReceberResumoDTO;
import com.sandrojam.controlevendas.dto.DashboardVendasDTO;
import com.sandrojam.controlevendas.dto.FluxoCaixaDTO;
import com.sandrojam.controlevendas.dto.FluxoCaixaDiaDTO;
import com.sandrojam.controlevendas.dto.ProdutoMaisVendidoDTO;
import com.sandrojam.controlevendas.dto.RelatorioFinanceiroDTO;
import com.sandrojam.controlevendas.dto.VendaPorDiaDTO;
import com.sandrojam.controlevendas.model.ItemVenda;
import com.sandrojam.controlevendas.model.Pagamento;
import com.sandrojam.controlevendas.model.RecebimentoVenda;
import com.sandrojam.controlevendas.model.StatusPagamento;
import com.sandrojam.controlevendas.model.StatusVenda;
import com.sandrojam.controlevendas.model.Venda;
import com.sandrojam.controlevendas.repository.PagamentoRepository;
import com.sandrojam.controlevendas.repository.RecebimentoVendaRepository;
import com.sandrojam.controlevendas.repository.VendaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

/**
 * Relatórios agregados (somente leitura) usados pelas telas de Dashboard de Vendas e Financeiro.
 * Não altera nenhum dado — só lê Venda, Pagamento e RecebimentoVenda e monta os totais.
 */
@Service
@Transactional(readOnly = true)
public class RelatorioService {

    private static final int JANELA_PADRAO_DIAS = 29; // período padrão = últimos 30 dias (hoje - 29 até hoje)

    private final VendaRepository vendaRepository;
    private final PagamentoRepository pagamentoRepository;
    private final RecebimentoVendaRepository recebimentoVendaRepository;
    private final VendaService vendaService;

    public RelatorioService(VendaRepository vendaRepository, PagamentoRepository pagamentoRepository,
                             RecebimentoVendaRepository recebimentoVendaRepository, VendaService vendaService) {
        this.vendaRepository = vendaRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.recebimentoVendaRepository = recebimentoVendaRepository;
        this.vendaService = vendaService;
    }

    /**
     * Totais de vendas do período (não canceladas): faturamento, ticket médio, ranking dos 10
     * produtos mais vendidos e a série diária de faturamento (para o gráfico).
     * inicio/fim nulos = últimos 30 dias.
     */
    public DashboardVendasDTO gerarDashboardVendas(LocalDate inicio, LocalDate fim) {
        LocalDate fimReal = fim != null ? fim : LocalDate.now();
        LocalDate inicioReal = inicio != null ? inicio : fimReal.minusDays(JANELA_PADRAO_DIAS);

        LocalDateTime inicioDateTime = inicioReal.atStartOfDay();
        LocalDateTime fimDateTime = fimReal.atTime(LocalTime.MAX);

        List<Venda> vendas = vendaRepository.findByStatusNotAndDataVendaBetween(
                StatusVenda.CANCELADA, inicioDateTime, fimDateTime);

        DashboardVendasDTO dto = new DashboardVendasDTO();
        dto.setInicio(inicioReal);
        dto.setFim(fimReal);
        dto.setQuantidadeVendas((long) vendas.size());

        BigDecimal faturamentoTotal = somar(vendas, Venda::getValorTotal);
        dto.setFaturamentoTotal(faturamentoTotal);
        dto.setTicketMedio(vendas.isEmpty()
                ? BigDecimal.ZERO
                : faturamentoTotal.divide(BigDecimal.valueOf(vendas.size()), 2, RoundingMode.HALF_UP));

        int totalItens = vendas.stream()
                .flatMap(v -> v.getItens().stream())
                .mapToInt(ItemVenda::getQuantidade)
                .sum();
        dto.setTotalItensVendidos(totalItens);

        dto.setProdutosMaisVendidos(montarRankingProdutos(vendas));
        dto.setVendasPorDia(montarVendasPorDia(vendas));

        return dto;
    }

    /**
     * Relatório financeiro completo: contas a pagar, contas a receber e fluxo de caixa,
     * todos calculados com a mesma janela de período (inicio/fim nulos = últimos 30 dias).
     */
    public RelatorioFinanceiroDTO gerarRelatorioFinanceiro(LocalDate inicio, LocalDate fim) {
        RelatorioFinanceiroDTO dto = new RelatorioFinanceiroDTO();
        dto.setContasAPagar(gerarContasAPagar(inicio, fim));
        dto.setContasAReceber(gerarContasAReceber(inicio, fim));
        dto.setFluxoCaixa(gerarFluxoCaixa(inicio, fim));
        return dto;
    }

    /**
     * Situação das contas a pagar HOJE (pendente/vencido/a vencer não dependem de período —
     * são uma "foto" do momento da consulta) mais o total efetivamente pago dentro do período.
     */
    public ContasAPagarResumoDTO gerarContasAPagar(LocalDate inicio, LocalDate fim) {
        LocalDate hoje = LocalDate.now();
        List<Pagamento> naoCancelados = pagamentoRepository.findByStatusNot(StatusPagamento.CANCELADO);

        List<Pagamento> pendentes = naoCancelados.stream()
                .filter(p -> p.getStatus() == StatusPagamento.PENDENTE)
                .toList();
        List<Pagamento> vencidos = pendentes.stream()
                .filter(p -> p.getDataVencimento().isBefore(hoje))
                .toList();

        BigDecimal totalPendente = somar(pendentes, Pagamento::getValorAPagar);
        BigDecimal totalVencido = somar(vencidos, Pagamento::getValorAPagar);

        ContasAPagarResumoDTO dto = new ContasAPagarResumoDTO();
        dto.setTotalPendente(totalPendente);
        dto.setTotalVencido(totalVencido);
        dto.setTotalAVencer(totalPendente.subtract(totalVencido));
        dto.setQuantidadePendentes(pendentes.size());
        dto.setQuantidadeVencidas(vencidos.size());

        LocalDate[] periodo = resolverPeriodo(inicio, fim);
        List<Pagamento> pagosNoPeriodo = pagamentoRepository.findByStatusAndDataPagamentoBetween(
                StatusPagamento.PAGO, periodo[0], periodo[1]);
        dto.setTotalPagoNoPeriodo(somar(pagosNoPeriodo, Pagamento::getValorAPagar));

        return dto;
    }

    /**
     * Situação das contas a receber HOJE (dívida em aberto dos clientes, mesma base da tela de
     * Consulta de Vendas) mais o total efetivamente recebido dentro do período.
     */
    public ContasAReceberResumoDTO gerarContasAReceber(LocalDate inicio, LocalDate fim) {
        List<ClienteDevedorDTO> devedores = vendaService.listarResumoDevedores();

        BigDecimal totalDevido = devedores.stream()
                .map(ClienteDevedorDTO::getTotalDevido)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long qtdVendasPendentes = devedores.stream()
                .mapToLong(ClienteDevedorDTO::getQuantidadeVendasPendentes)
                .sum();

        ContasAReceberResumoDTO dto = new ContasAReceberResumoDTO();
        dto.setTotalDevido(totalDevido);
        dto.setQuantidadeClientesDevedores(devedores.size());
        dto.setQuantidadeVendasPendentes(qtdVendasPendentes);

        LocalDate[] periodo = resolverPeriodo(inicio, fim);
        List<RecebimentoVenda> recebimentosNoPeriodo =
                recebimentoVendaRepository.findByDataRecebimentoBetween(periodo[0], periodo[1]);
        dto.setTotalRecebidoNoPeriodo(somar(recebimentosNoPeriodo, RecebimentoVenda::getValor));

        return dto;
    }

    /**
     * Entradas (recebimentos de venda, fiado quitado ou à vista) menos saídas (pagamentos
     * efetivamente pagos) dentro do período, com a série diária para o gráfico.
     */
    public FluxoCaixaDTO gerarFluxoCaixa(LocalDate inicio, LocalDate fim) {
        LocalDate[] periodo = resolverPeriodo(inicio, fim);
        LocalDate inicioReal = periodo[0];
        LocalDate fimReal = periodo[1];

        List<RecebimentoVenda> recebimentos = recebimentoVendaRepository.findByDataRecebimentoBetween(inicioReal, fimReal);
        List<Pagamento> pagos = pagamentoRepository.findByStatusAndDataPagamentoBetween(StatusPagamento.PAGO, inicioReal, fimReal);

        Map<LocalDate, FluxoCaixaDiaDTO> porDia = new TreeMap<>();
        for (RecebimentoVenda recebimento : recebimentos) {
            FluxoCaixaDiaDTO dia = obterOuCriarDia(porDia, recebimento.getDataRecebimento());
            dia.setEntradas(dia.getEntradas().add(recebimento.getValor()));
        }
        for (Pagamento pagamento : pagos) {
            FluxoCaixaDiaDTO dia = obterOuCriarDia(porDia, pagamento.getDataPagamento());
            dia.setSaidas(dia.getSaidas().add(pagamento.getValorAPagar()));
        }
        porDia.values().forEach(dia -> dia.setSaldoDia(dia.getEntradas().subtract(dia.getSaidas())));

        BigDecimal totalEntradas = somar(recebimentos, RecebimentoVenda::getValor);
        BigDecimal totalSaidas = somar(pagos, Pagamento::getValorAPagar);

        FluxoCaixaDTO dto = new FluxoCaixaDTO();
        dto.setInicio(inicioReal);
        dto.setFim(fimReal);
        dto.setTotalEntradas(totalEntradas);
        dto.setTotalSaidas(totalSaidas);
        dto.setSaldoPeriodo(totalEntradas.subtract(totalSaidas));
        dto.setDias(new ArrayList<>(porDia.values()));
        return dto;
    }

    private List<ProdutoMaisVendidoDTO> montarRankingProdutos(List<Venda> vendas) {
        Map<Long, ProdutoMaisVendidoDTO> ranking = new LinkedHashMap<>();

        for (Venda venda : vendas) {
            for (ItemVenda item : venda.getItens()) {
                ProdutoMaisVendidoDTO acumulado = ranking.computeIfAbsent(item.getProduto().getId(), id -> {
                    ProdutoMaisVendidoDTO novo = new ProdutoMaisVendidoDTO();
                    novo.setProdutoId(id);
                    novo.setProdutoNome(item.getProduto().getNome());
                    novo.setQuantidadeVendida(0);
                    novo.setValorTotal(BigDecimal.ZERO);
                    return novo;
                });
                acumulado.setQuantidadeVendida(acumulado.getQuantidadeVendida() + item.getQuantidade());
                acumulado.setValorTotal(acumulado.getValorTotal().add(item.getSubtotal()));
            }
        }

        return ranking.values().stream()
                .sorted(Comparator.comparing(ProdutoMaisVendidoDTO::getQuantidadeVendida).reversed())
                .limit(10)
                .toList();
    }

    private List<VendaPorDiaDTO> montarVendasPorDia(List<Venda> vendas) {
        Map<LocalDate, VendaPorDiaDTO> porDia = new TreeMap<>();

        for (Venda venda : vendas) {
            LocalDate data = venda.getDataVenda().toLocalDate();
            VendaPorDiaDTO acumulado = porDia.computeIfAbsent(data, d -> {
                VendaPorDiaDTO novo = new VendaPorDiaDTO();
                novo.setData(d);
                novo.setQuantidadeVendas(0L);
                novo.setValorTotal(BigDecimal.ZERO);
                return novo;
            });
            acumulado.setQuantidadeVendas(acumulado.getQuantidadeVendas() + 1);
            acumulado.setValorTotal(acumulado.getValorTotal().add(venda.getValorTotal()));
        }

        return new ArrayList<>(porDia.values());
    }

    private FluxoCaixaDiaDTO obterOuCriarDia(Map<LocalDate, FluxoCaixaDiaDTO> porDia, LocalDate data) {
        return porDia.computeIfAbsent(data, d -> {
            FluxoCaixaDiaDTO novo = new FluxoCaixaDiaDTO();
            novo.setData(d);
            novo.setEntradas(BigDecimal.ZERO);
            novo.setSaidas(BigDecimal.ZERO);
            novo.setSaldoDia(BigDecimal.ZERO);
            return novo;
        });
    }

    /** Resolve o período pedido aplicando o padrão de últimos 30 dias quando vier nulo. */
    private LocalDate[] resolverPeriodo(LocalDate inicio, LocalDate fim) {
        LocalDate fimReal = fim != null ? fim : LocalDate.now();
        LocalDate inicioReal = inicio != null ? inicio : fimReal.minusDays(JANELA_PADRAO_DIAS);
        return new LocalDate[] { inicioReal, fimReal };
    }

    private <T> BigDecimal somar(List<T> itens, Function<T, BigDecimal> extrator) {
        return itens.stream().map(extrator).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

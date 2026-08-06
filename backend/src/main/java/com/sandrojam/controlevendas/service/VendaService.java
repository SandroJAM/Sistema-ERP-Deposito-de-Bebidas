package com.sandrojam.controlevendas.service;

import com.sandrojam.controlevendas.dto.ClienteDevedorDTO;
import com.sandrojam.controlevendas.dto.ExtratoClienteDTO;
import com.sandrojam.controlevendas.dto.ItemLancamentoDTO;
import com.sandrojam.controlevendas.dto.ItemVendaDTO;
import com.sandrojam.controlevendas.dto.LancamentoExtratoDTO;
import com.sandrojam.controlevendas.dto.RecebimentoVendaDTO;
import com.sandrojam.controlevendas.dto.VendaDTO;
import com.sandrojam.controlevendas.exception.EstoqueInsuficienteException;
import com.sandrojam.controlevendas.exception.RegraNegocioException;
import com.sandrojam.controlevendas.exception.ResourceNotFoundException;
import com.sandrojam.controlevendas.model.*;
import com.sandrojam.controlevendas.repository.ClienteRepository;
import com.sandrojam.controlevendas.repository.ProdutoRepository;
import com.sandrojam.controlevendas.repository.RecebimentoVendaRepository;
import com.sandrojam.controlevendas.repository.UsuarioRepository;
import com.sandrojam.controlevendas.repository.VendaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class VendaService {

    private final VendaRepository vendaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final RecebimentoVendaRepository recebimentoVendaRepository;
    private final EmpresaService empresaService;

    public VendaService(VendaRepository vendaRepository, UsuarioRepository usuarioRepository,
                         ClienteRepository clienteRepository, ProdutoRepository produtoRepository,
                         RecebimentoVendaRepository recebimentoVendaRepository, EmpresaService empresaService) {
        this.vendaRepository = vendaRepository;
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.recebimentoVendaRepository = recebimentoVendaRepository;
        this.empresaService = empresaService;
    }

    @Transactional(readOnly = true)
    public List<VendaDTO> listarTodas() {
        return vendaRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public VendaDTO buscarPorId(Long id) {
        return toDTO(buscarEntidade(id));
    }

    /**
     * Cria a venda, calcula os subtotais/total com base no preço ATUAL do produto
     * (nunca confia em preço vindo do cliente) e abate o estoque de cada item.
     * Se algum produto não tiver estoque suficiente, a transação inteira é revertida.
     */
    public VendaDTO criar(VendaDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + dto.getUsuarioId()));

        Cliente cliente = null;
        if (dto.getClienteId() != null) {
            cliente = clienteRepository.findById(dto.getClienteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado: " + dto.getClienteId()));
        }

        Venda venda = new Venda();
        venda.setUsuario(usuario);
        venda.setCliente(cliente);
        venda.setStatus(StatusVenda.FINALIZADA);

        BigDecimal valorTotal = BigDecimal.ZERO;

        for (ItemVendaDTO itemDto : dto.getItens()) {
            Produto produto = produtoRepository.findById(itemDto.getProdutoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + itemDto.getProdutoId()));

            if (produto.getEstoqueAtual() < itemDto.getQuantidade()) {
                throw new EstoqueInsuficienteException(
                        "Estoque insuficiente para \"" + produto.getNome() + "\". Disponível: "
                                + produto.getEstoqueAtual() + ", solicitado: " + itemDto.getQuantidade());
            }

            ItemVenda item = new ItemVenda();
            item.setProduto(produto);
            item.setQuantidade(itemDto.getQuantidade());
            item.setPrecoUnitario(produto.getPreco());
            item.calcularSubtotal();

            venda.adicionarItem(item);
            valorTotal = valorTotal.add(item.getSubtotal());

            // Dirty checking do Hibernate salva essa alteração junto, sem precisar chamar save() aqui.
            produto.setEstoqueAtual(produto.getEstoqueAtual() - itemDto.getQuantidade());
        }

        venda.setValorTotal(valorTotal);

        return toDTO(vendaRepository.save(venda));
    }

    /**
     * Cancela a venda e devolve a quantidade de cada item ao estoque do produto.
     */
    public VendaDTO cancelar(Long id) {
        Venda venda = buscarEntidade(id);

        if (venda.getStatus() == StatusVenda.CANCELADA) {
            return toDTO(venda);
        }

        for (ItemVenda item : venda.getItens()) {
            Produto produto = item.getProduto();
            produto.setEstoqueAtual(produto.getEstoqueAtual() + item.getQuantidade());
        }

        venda.setStatus(StatusVenda.CANCELADA);

        return toDTO(vendaRepository.save(venda));
    }

    /**
     * Um registro por cliente com o total que ele ainda deve (soma do saldo devedor de todas as
     * vendas não canceladas). Só entram clientes com saldo devedor maior que zero — é a base da
     * tela de Consulta de Vendas.
     */
    @Transactional(readOnly = true)
    public List<ClienteDevedorDTO> listarResumoDevedores() {
        List<Venda> vendas = vendaRepository.findByClienteIsNotNullAndStatusNot(StatusVenda.CANCELADA);

        return vendas.stream()
                .map(Venda::getCliente)
                .distinct()
                .map(cliente -> {
                    List<Venda> vendasDoCliente = vendas.stream()
                            .filter(v -> v.getCliente().getId().equals(cliente.getId()))
                            .toList();

                    BigDecimal totalDevido = vendasDoCliente.stream()
                            .map(Venda::getValorDevido)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    long qtdPendentes = vendasDoCliente.stream()
                            .filter(v -> v.getValorDevido().compareTo(BigDecimal.ZERO) > 0)
                            .count();

                    ClienteDevedorDTO dto = new ClienteDevedorDTO();
                    dto.setClienteId(cliente.getId());
                    dto.setClienteNome(cliente.getNome());
                    dto.setClienteTelefone(cliente.getTelefone());
                    dto.setTotalDevido(totalDevido);
                    dto.setQuantidadeVendasPendentes(qtdPendentes);
                    return dto;
                })
                .filter(dto -> dto.getTotalDevido().compareTo(BigDecimal.ZERO) > 0)
                .sorted(Comparator.comparing(ClienteDevedorDTO::getTotalDevido).reversed())
                .toList();
    }

    /**
     * Extrato do cliente, no formato "bobina de calculadora": lançamentos em ordem cronológica,
     * cada venda soma na dívida e cada recebimento abate, com saldo acumulado após cada linha.
     *
     * - somenteDevido=true: ignora o período e traz só as vendas ainda não quitadas (e os
     *   recebimentos parciais que elas já tiveram) — "tudo que ainda deve".
     * - somenteDevido=false: traz as vendas e recebimentos dentro do período informado
     *   (histórico de compras); se inicio/fim vierem nulos, considera todo o histórico.
     * - statusPagamento (opcional): filtra as vendas consideradas por PENDENTE/PARCIAL/PAGO,
     *   pra permitir emitir o histórico já filtrado pela situação de pagamento pesquisada.
     */
    @Transactional(readOnly = true)
    public ExtratoClienteDTO buscarExtratoCliente(Long clienteId, LocalDate inicio, LocalDate fim,
                                                   boolean somenteDevido, StatusPagamentoVenda statusPagamento) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado: " + clienteId));

        List<Venda> todasAsVendas = vendaRepository.findByCliente_IdOrderByDataVendaDesc(clienteId).stream()
                .filter(v -> v.getStatus() != StatusVenda.CANCELADA)
                .toList();

        List<Venda> vendasConsideradas;
        if (somenteDevido) {
            vendasConsideradas = todasAsVendas.stream()
                    .filter(v -> v.getValorDevido().compareTo(BigDecimal.ZERO) > 0)
                    .toList();
        } else {
            vendasConsideradas = todasAsVendas.stream()
                    .filter(v -> dentroDoPeriodo(v.getDataVenda().toLocalDate(), inicio, fim))
                    .toList();
        }

        if (statusPagamento != null) {
            vendasConsideradas = vendasConsideradas.stream()
                    .filter(v -> v.getStatusPagamento() == statusPagamento)
                    .toList();
        }

        List<LancamentoExtratoDTO> lancamentos = new ArrayList<>();

        for (Venda venda : vendasConsideradas) {
            LancamentoExtratoDTO lancamentoVenda = new LancamentoExtratoDTO();
            lancamentoVenda.setTipo("VENDA");
            lancamentoVenda.setData(venda.getDataVenda().toLocalDate());
            lancamentoVenda.setDescricao("Venda #" + venda.getId());
            lancamentoVenda.setVendaId(venda.getId());
            lancamentoVenda.setValor(venda.getValorTotal());
            lancamentoVenda.setItens(venda.getItens().stream().map(this::toItemLancamentoDTO).toList());
            lancamentos.add(lancamentoVenda);

            for (RecebimentoVenda recebimento : venda.getRecebimentos()) {
                if (somenteDevido || dentroDoPeriodo(recebimento.getDataRecebimento(), inicio, fim)) {
                    LancamentoExtratoDTO lancamentoRecebimento = new LancamentoExtratoDTO();
                    lancamentoRecebimento.setTipo("RECEBIMENTO");
                    lancamentoRecebimento.setData(recebimento.getDataRecebimento());
                    lancamentoRecebimento.setDescricao(
                            recebimento.getObservacao() != null && !recebimento.getObservacao().isBlank()
                                    ? recebimento.getObservacao()
                                    : "Recebimento da venda #" + venda.getId());
                    lancamentoRecebimento.setVendaId(venda.getId());
                    lancamentoRecebimento.setValor(recebimento.getValor().negate());
                    lancamentos.add(lancamentoRecebimento);
                }
            }
        }

        lancamentos.sort(Comparator.comparing(LancamentoExtratoDTO::getData)
                .thenComparing(l -> "VENDA".equals(l.getTipo()) ? 0 : 1));

        BigDecimal saldoCorrente = BigDecimal.ZERO;
        for (LancamentoExtratoDTO lancamento : lancamentos) {
            saldoCorrente = saldoCorrente.add(lancamento.getValor());
            lancamento.setSaldoAcumulado(saldoCorrente);
        }

        BigDecimal totalDevidoGeral = todasAsVendas.stream()
                .map(Venda::getValorDevido)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        ExtratoClienteDTO extrato = new ExtratoClienteDTO();
        extrato.setClienteId(cliente.getId());
        extrato.setClienteNome(cliente.getNome());
        extrato.setTotalDevidoGeral(totalDevidoGeral);
        extrato.setLancamentos(lancamentos);
        empresaService.buscarAtual().ifPresent(empresa -> {
            extrato.setEmpresaNome(empresa.getNome());
            extrato.setEmpresaEndereco(empresa.getEndereco());
            extrato.setEmpresaTelefone(empresa.getTelefone());
        });
        return extrato;
    }

    private boolean dentroDoPeriodo(LocalDate data, LocalDate inicio, LocalDate fim) {
        if (inicio != null && data.isBefore(inicio)) {
            return false;
        }
        return fim == null || !data.isAfter(fim);
    }

    /**
     * Registra uma baixa (total ou parcial) em uma venda. Não permite receber mais do que o
     * saldo devedor da venda, nem lançar em venda cancelada. Atualiza automaticamente o
     * statusPagamento da venda (PENDENTE / PARCIAL / PAGO) com base no total já recebido.
     */
    public VendaDTO registrarRecebimento(Long vendaId, RecebimentoVendaDTO dto) {
        Venda venda = buscarEntidade(vendaId);

        if (venda.getStatus() == StatusVenda.CANCELADA) {
            throw new RegraNegocioException("Não é possível registrar recebimento em uma venda cancelada.");
        }

        BigDecimal valorDevido = venda.getValorDevido();
        if (dto.getValor().compareTo(valorDevido) > 0) {
            throw new RegraNegocioException(
                    "Valor recebido (R$ " + dto.getValor() + ") maior que o saldo devedor da venda (R$ " + valorDevido + ").");
        }

        RecebimentoVenda recebimento = new RecebimentoVenda();
        recebimento.setVenda(venda);
        recebimento.setDataRecebimento(dto.getDataRecebimento() != null ? dto.getDataRecebimento() : LocalDate.now());
        recebimento.setValor(dto.getValor());
        recebimento.setObservacao(dto.getObservacao());
        venda.getRecebimentos().add(recebimento);

        atualizarStatusPagamento(venda);

        return toDTO(vendaRepository.save(venda));
    }

    @Transactional(readOnly = true)
    public List<RecebimentoVendaDTO> listarRecebimentos(Long vendaId) {
        return recebimentoVendaRepository.findByVendaIdOrderByDataRecebimentoAsc(vendaId).stream()
                .map(this::toRecebimentoDTO)
                .toList();
    }

    private void atualizarStatusPagamento(Venda venda) {
        BigDecimal pago = venda.getValorPago();
        if (pago.compareTo(BigDecimal.ZERO) <= 0) {
            venda.setStatusPagamento(StatusPagamentoVenda.PENDENTE);
        } else if (pago.compareTo(venda.getValorTotal()) >= 0) {
            venda.setStatusPagamento(StatusPagamentoVenda.PAGO);
        } else {
            venda.setStatusPagamento(StatusPagamentoVenda.PARCIAL);
        }
    }

    private RecebimentoVendaDTO toRecebimentoDTO(RecebimentoVenda recebimento) {
        RecebimentoVendaDTO dto = new RecebimentoVendaDTO();
        dto.setId(recebimento.getId());
        dto.setVendaId(recebimento.getVenda().getId());
        dto.setDataRecebimento(recebimento.getDataRecebimento());
        dto.setValor(recebimento.getValor());
        dto.setObservacao(recebimento.getObservacao());
        return dto;
    }

    private ItemLancamentoDTO toItemLancamentoDTO(ItemVenda item) {
        ItemLancamentoDTO dto = new ItemLancamentoDTO();
        dto.setProdutoNome(item.getProduto().getNome());
        dto.setQuantidade(item.getQuantidade());
        dto.setPrecoUnitario(item.getPrecoUnitario());
        dto.setSubtotal(item.getSubtotal());
        return dto;
    }

    private Venda buscarEntidade(Long id) {
        return vendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venda não encontrada: " + id));
    }

    private VendaDTO toDTO(Venda venda) {
        VendaDTO dto = new VendaDTO();
        dto.setId(venda.getId());
        dto.setUsuarioId(venda.getUsuario().getId());
        dto.setClienteId(venda.getCliente() != null ? venda.getCliente().getId() : null);
        dto.setDataVenda(venda.getDataVenda());
        dto.setStatus(venda.getStatus().name());
        dto.setValorTotal(venda.getValorTotal());
        dto.setStatusPagamento(venda.getStatusPagamento().name());
        dto.setValorPago(venda.getValorPago());
        dto.setValorDevido(venda.getValorDevido());
        dto.setItens(venda.getItens().stream().map(this::toItemDTO).toList());
        return dto;
    }

    private ItemVendaDTO toItemDTO(ItemVenda item) {
        ItemVendaDTO dto = new ItemVendaDTO();
        dto.setId(item.getId());
        dto.setProdutoId(item.getProduto().getId());
        dto.setProdutoNome(item.getProduto().getNome());
        dto.setQuantidade(item.getQuantidade());
        dto.setPrecoUnitario(item.getPrecoUnitario());
        dto.setSubtotal(item.getSubtotal());
        return dto;
    }
}

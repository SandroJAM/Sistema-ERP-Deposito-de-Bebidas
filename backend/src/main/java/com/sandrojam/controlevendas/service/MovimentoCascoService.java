package com.sandrojam.controlevendas.service;

import com.sandrojam.controlevendas.dto.MovimentoCascoDTO;
import com.sandrojam.controlevendas.dto.SaldoCascoDTO;
import com.sandrojam.controlevendas.exception.RegraNegocioException;
import com.sandrojam.controlevendas.exception.ResourceNotFoundException;
import com.sandrojam.controlevendas.model.Cliente;
import com.sandrojam.controlevendas.model.MovimentoCasco;
import com.sandrojam.controlevendas.model.TipoCasco;
import com.sandrojam.controlevendas.model.TipoMovimentoCasco;
import com.sandrojam.controlevendas.repository.ClienteRepository;
import com.sandrojam.controlevendas.repository.MovimentoCascoRepository;
import com.sandrojam.controlevendas.repository.TipoCascoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Controle de vasilhame/casco: cada saída registra cascos que foram com o cliente (venda ou
 * empréstimo avulso) e cada devolução abate esse saldo. O saldo em aberto nunca fica negativo —
 * não é permitido devolver mais do que o cliente tem pendente.
 */
@Service
@Transactional
public class MovimentoCascoService {

    private final MovimentoCascoRepository movimentoCascoRepository;
    private final ClienteRepository clienteRepository;
    private final TipoCascoRepository tipoCascoRepository;

    public MovimentoCascoService(MovimentoCascoRepository movimentoCascoRepository, ClienteRepository clienteRepository,
                                  TipoCascoRepository tipoCascoRepository) {
        this.movimentoCascoRepository = movimentoCascoRepository;
        this.clienteRepository = clienteRepository;
        this.tipoCascoRepository = tipoCascoRepository;
    }

    @Transactional(readOnly = true)
    public List<MovimentoCascoDTO> listarTodos() {
        return movimentoCascoRepository.findAllByOrderByDataDesc().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<MovimentoCascoDTO> listarPorCliente(Long clienteId) {
        return movimentoCascoRepository.findByCliente_IdOrderByDataDesc(clienteId).stream().map(this::toDTO).toList();
    }

    /**
     * Registra uma saída (casco foi com o cliente) ou devolução. Para devolução, valida que
     * o cliente não está devolvendo mais do que tem em aberto para aquele tipo de casco.
     */
    public MovimentoCascoDTO registrar(MovimentoCascoDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado: " + dto.getClienteId()));
        TipoCasco tipoCasco = tipoCascoRepository.findById(dto.getTipoCascoId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de casco não encontrado: " + dto.getTipoCascoId()));

        TipoMovimentoCasco tipoMovimento = TipoMovimentoCasco.valueOf(dto.getTipoMovimento());

        if (tipoMovimento == TipoMovimentoCasco.DEVOLUCAO) {
            int saldoAtual = calcularSaldo(dto.getClienteId(), dto.getTipoCascoId());
            if (dto.getQuantidade() > saldoAtual) {
                throw new RegraNegocioException(
                        "Cliente tem apenas " + saldoAtual + " casco(s) \"" + tipoCasco.getNome()
                                + "\" em aberto — não é possível devolver " + dto.getQuantidade() + ".");
            }
        }

        MovimentoCasco movimento = new MovimentoCasco();
        movimento.setCliente(cliente);
        movimento.setTipoCasco(tipoCasco);
        movimento.setTipoMovimento(tipoMovimento);
        movimento.setQuantidade(dto.getQuantidade());
        movimento.setData(dto.getData() != null ? dto.getData() : LocalDate.now());
        movimento.setVendaId(dto.getVendaId());
        movimento.setObservacao(dto.getObservacao());

        return toDTO(movimentoCascoRepository.save(movimento));
    }

    public void excluir(Long id) {
        MovimentoCasco movimento = movimentoCascoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimento de casco não encontrado: " + id));
        movimentoCascoRepository.delete(movimento);
    }

    /**
     * Um registro por combinação cliente + tipo de casco com saldo em aberto maior que zero —
     * "quem está com casco emprestado". Base da tela de controle de vasilhame.
     */
    @Transactional(readOnly = true)
    public List<SaldoCascoDTO> listarSaldosEmAberto() {
        List<MovimentoCasco> todos = movimentoCascoRepository.findAllByOrderByDataDesc();

        Map<String, SaldoCascoDTO> saldos = new LinkedHashMap<>();
        for (MovimentoCasco movimento : todos) {
            String chave = movimento.getCliente().getId() + "-" + movimento.getTipoCasco().getId();
            SaldoCascoDTO saldo = saldos.computeIfAbsent(chave, k -> {
                SaldoCascoDTO novo = new SaldoCascoDTO();
                novo.setClienteId(movimento.getCliente().getId());
                novo.setClienteNome(movimento.getCliente().getNome());
                novo.setTipoCascoId(movimento.getTipoCasco().getId());
                novo.setTipoCascoNome(movimento.getTipoCasco().getNome());
                novo.setQuantidadeEmAberto(0);
                return novo;
            });

            int delta = movimento.getTipoMovimento() == TipoMovimentoCasco.SAIDA
                    ? movimento.getQuantidade()
                    : -movimento.getQuantidade();
            saldo.setQuantidadeEmAberto(saldo.getQuantidadeEmAberto() + delta);
        }

        return new ArrayList<>(saldos.values()).stream()
                .filter(s -> s.getQuantidadeEmAberto() > 0)
                .sorted(Comparator.comparing(SaldoCascoDTO::getClienteNome))
                .toList();
    }

    private int calcularSaldo(Long clienteId, Long tipoCascoId) {
        return movimentoCascoRepository.findByCliente_IdAndTipoCasco_IdOrderByDataAsc(clienteId, tipoCascoId).stream()
                .mapToInt(m -> m.getTipoMovimento() == TipoMovimentoCasco.SAIDA ? m.getQuantidade() : -m.getQuantidade())
                .sum();
    }

    private MovimentoCascoDTO toDTO(MovimentoCasco movimento) {
        MovimentoCascoDTO dto = new MovimentoCascoDTO();
        dto.setId(movimento.getId());
        dto.setClienteId(movimento.getCliente().getId());
        dto.setClienteNome(movimento.getCliente().getNome());
        dto.setTipoCascoId(movimento.getTipoCasco().getId());
        dto.setTipoCascoNome(movimento.getTipoCasco().getNome());
        dto.setTipoMovimento(movimento.getTipoMovimento().name());
        dto.setQuantidade(movimento.getQuantidade());
        dto.setData(movimento.getData());
        dto.setVendaId(movimento.getVendaId());
        dto.setObservacao(movimento.getObservacao());
        return dto;
    }
}

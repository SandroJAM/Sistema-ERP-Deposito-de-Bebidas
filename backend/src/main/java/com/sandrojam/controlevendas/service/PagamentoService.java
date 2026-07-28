package com.sandrojam.controlevendas.service;

import com.sandrojam.controlevendas.dto.PagamentoDTO;
import com.sandrojam.controlevendas.exception.ResourceNotFoundException;
import com.sandrojam.controlevendas.model.Fornecedor;
import com.sandrojam.controlevendas.model.Pagamento;
import com.sandrojam.controlevendas.model.StatusPagamento;
import com.sandrojam.controlevendas.repository.PagamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;

    public PagamentoService(PagamentoRepository pagamentoRepository) {
        this.pagamentoRepository = pagamentoRepository;
    }

    /**
     * Lista pagamentos combinando filtros opcionais — qualquer parâmetro nulo equivale a "todos".
     */
    @Transactional(readOnly = true)
    public List<PagamentoDTO> listarComFiltros(Long fornecedorId, StatusPagamento status,
                                                LocalDate vencimentoDe, LocalDate vencimentoAte) {
        return pagamentoRepository.buscarComFiltros(fornecedorId, status, vencimentoDe, vencimentoAte).stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public PagamentoDTO buscarPorId(Long id) {
        return toDTO(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public List<PagamentoDTO> listarPorNota(Long notaEntradaId) {
        return pagamentoRepository.findByNotaEntradaId(notaEntradaId).stream()
                .map(this::toDTO)
                .toList();
    }

    public PagamentoDTO marcarComoPago(Long id) {
        Pagamento pagamento = buscarEntidade(id);
        pagamento.setStatus(StatusPagamento.PAGO);
        return toDTO(pagamentoRepository.save(pagamento));
    }

    private Pagamento buscarEntidade(Long id) {
        return pagamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado: " + id));
    }

    private PagamentoDTO toDTO(Pagamento pagamento) {
        Fornecedor fornecedor = pagamento.getNotaEntrada().getFornecedor();

        PagamentoDTO dto = new PagamentoDTO();
        dto.setId(pagamento.getId());
        dto.setNotaEntradaId(pagamento.getNotaEntrada().getId());
        dto.setFornecedorId(fornecedor != null ? fornecedor.getId() : null);
        dto.setFornecedorNome(fornecedor != null ? fornecedor.getNome() : null);
        dto.setNumeroFatura(pagamento.getNumeroFatura());
        dto.setNumeroParcela(pagamento.getNumeroParcela());
        dto.setDataEmissao(pagamento.getDataEmissao());
        dto.setValorAPagar(pagamento.getValorAPagar());
        dto.setDataVencimento(pagamento.getDataVencimento());
        dto.setStatus(pagamento.getStatus().name());
        return dto;
    }
}

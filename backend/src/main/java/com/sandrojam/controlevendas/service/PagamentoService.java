package com.sandrojam.controlevendas.service;

import com.sandrojam.controlevendas.dto.PagamentoDTO;
import com.sandrojam.controlevendas.exception.RegraNegocioException;
import com.sandrojam.controlevendas.exception.ResourceNotFoundException;
import com.sandrojam.controlevendas.model.Fornecedor;
import com.sandrojam.controlevendas.model.NotaEntrada;
import com.sandrojam.controlevendas.model.Pagamento;
import com.sandrojam.controlevendas.model.StatusPagamento;
import com.sandrojam.controlevendas.repository.FornecedorRepository;
import com.sandrojam.controlevendas.repository.NotaEntradaRepository;
import com.sandrojam.controlevendas.repository.PagamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final NotaEntradaRepository notaEntradaRepository;
    private final FornecedorRepository fornecedorRepository;

    public PagamentoService(PagamentoRepository pagamentoRepository, NotaEntradaRepository notaEntradaRepository,
                             FornecedorRepository fornecedorRepository) {
        this.pagamentoRepository = pagamentoRepository;
        this.notaEntradaRepository = notaEntradaRepository;
        this.fornecedorRepository = fornecedorRepository;
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
        pagamento.setDataPagamento(LocalDate.now());
        return toDTO(pagamentoRepository.save(pagamento));
    }

    /**
     * Cria um pagamento. Se notaEntradaId for informado, vincula à nota (fornecedor passa a ser o
     * dela — o parâmetro fornecedorId do DTO é ignorado nesse caso); caso contrário, cria um
     * pagamento avulso, exigindo fornecedorId.
     */
    public PagamentoDTO criar(PagamentoDTO dto) {
        Pagamento pagamento = new Pagamento();

        if (dto.getNotaEntradaId() != null) {
            NotaEntrada nota = notaEntradaRepository.findById(dto.getNotaEntradaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Nota de entrada não encontrada: " + dto.getNotaEntradaId()));
            pagamento.setNotaEntrada(nota);
        } else {
            if (dto.getFornecedorId() == null) {
                throw new RegraNegocioException("Informe o fornecedor para um pagamento avulso (sem nota de entrada).");
            }
            Fornecedor fornecedor = fornecedorRepository.findById(dto.getFornecedorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado: " + dto.getFornecedorId()));
            pagamento.setFornecedor(fornecedor);
        }

        aplicarCamposEditaveis(pagamento, dto);
        return toDTO(pagamentoRepository.save(pagamento));
    }

    /**
     * Atualiza os dados de um pagamento, seja avulso ou gerado por nota de entrada. A origem
     * (vínculo com a nota) não é alterável por aqui: para pagamentos avulsos, o fornecedor pode
     * ser trocado; para os vinculados a uma nota, o fornecedor continua sendo o dela.
     */
    public PagamentoDTO atualizar(Long id, PagamentoDTO dto) {
        Pagamento pagamento = buscarEntidade(id);

        if (pagamento.getNotaEntrada() == null) {
            if (dto.getFornecedorId() == null) {
                throw new RegraNegocioException("Informe o fornecedor para um pagamento avulso (sem nota de entrada).");
            }
            Fornecedor fornecedor = fornecedorRepository.findById(dto.getFornecedorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado: " + dto.getFornecedorId()));
            pagamento.setFornecedor(fornecedor);
        }

        aplicarCamposEditaveis(pagamento, dto);
        return toDTO(pagamentoRepository.save(pagamento));
    }

    /** Exclusão restrita a ADMIN (ver @PreAuthorize no controller). */
    public void excluir(Long id) {
        Pagamento pagamento = buscarEntidade(id);
        pagamentoRepository.delete(pagamento);
    }

    private void aplicarCamposEditaveis(Pagamento pagamento, PagamentoDTO dto) {
        pagamento.setNumeroFatura(dto.getNumeroFatura());
        pagamento.setNumeroParcela(dto.getNumeroParcela());
        pagamento.setDataEmissao(dto.getDataEmissao());
        pagamento.setValorAPagar(dto.getValorAPagar());
        pagamento.setDataVencimento(dto.getDataVencimento());
        pagamento.setDescricao(dto.getDescricao());

        StatusPagamento novoStatus = dto.getStatus() != null ? StatusPagamento.valueOf(dto.getStatus()) : StatusPagamento.PENDENTE;
        // Mantém a data de pagamento coerente com o status: some se o pagamento deixar de estar
        // PAGO por essa tela, e é preenchida por marcarComoPago (não por aqui) quando fica PAGO.
        if (novoStatus != StatusPagamento.PAGO) {
            pagamento.setDataPagamento(null);
        } else if (pagamento.getDataPagamento() == null) {
            pagamento.setDataPagamento(LocalDate.now());
        }
        pagamento.setStatus(novoStatus);
    }

    private Pagamento buscarEntidade(Long id) {
        return pagamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado: " + id));
    }

    private PagamentoDTO toDTO(Pagamento pagamento) {
        NotaEntrada nota = pagamento.getNotaEntrada();
        Fornecedor fornecedor = nota != null ? nota.getFornecedor() : pagamento.getFornecedor();

        PagamentoDTO dto = new PagamentoDTO();
        dto.setId(pagamento.getId());
        dto.setNotaEntradaId(nota != null ? nota.getId() : null);
        dto.setFornecedorId(fornecedor != null ? fornecedor.getId() : null);
        dto.setFornecedorNome(fornecedor != null ? fornecedor.getNome() : null);
        dto.setNumeroFatura(pagamento.getNumeroFatura());
        dto.setNumeroParcela(pagamento.getNumeroParcela());
        dto.setDataEmissao(pagamento.getDataEmissao());
        dto.setValorAPagar(pagamento.getValorAPagar());
        dto.setDataVencimento(pagamento.getDataVencimento());
        dto.setDescricao(pagamento.getDescricao());
        dto.setStatus(pagamento.getStatus().name());
        dto.setDataPagamento(pagamento.getDataPagamento());
        dto.setOrigem(nota != null ? "NOTA_ENTRADA" : "AVULSO");
        return dto;
    }
}

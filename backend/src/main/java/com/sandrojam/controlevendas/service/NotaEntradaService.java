package com.sandrojam.controlevendas.service;

import com.sandrojam.controlevendas.dto.ItemNotaEntradaDTO;
import com.sandrojam.controlevendas.dto.NotaEntradaDTO;
import com.sandrojam.controlevendas.dto.PagamentoDTO;
import com.sandrojam.controlevendas.exception.DivergenciaValorNotaException;
import com.sandrojam.controlevendas.exception.ResourceNotFoundException;
import com.sandrojam.controlevendas.model.*;
import com.sandrojam.controlevendas.repository.FornecedorRepository;
import com.sandrojam.controlevendas.repository.NotaEntradaRepository;
import com.sandrojam.controlevendas.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional
public class NotaEntradaService {

    private static final int ESCALA_VALOR = 2;

    private final NotaEntradaRepository notaEntradaRepository;
    private final FornecedorRepository fornecedorRepository;
    private final ProdutoRepository produtoRepository;

    public NotaEntradaService(NotaEntradaRepository notaEntradaRepository, FornecedorRepository fornecedorRepository,
                               ProdutoRepository produtoRepository) {
        this.notaEntradaRepository = notaEntradaRepository;
        this.fornecedorRepository = fornecedorRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional(readOnly = true)
    public List<NotaEntradaDTO> listarTodas() {
        return notaEntradaRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public NotaEntradaDTO buscarPorId(Long id) {
        return toDTO(buscarEntidade(id));
    }

    /**
     * Cria a nota de entrada, valida que a soma dos itens bate exatamente com o valor
     * informado da nota, alimenta o estoque de cada produto envolvido e gera automaticamente
     * o pagamento (à vista, com vencimento da nota) associado.
     */
    public NotaEntradaDTO criar(NotaEntradaDTO dto) {
        Fornecedor fornecedor = null;
        if (dto.getFornecedorId() != null) {
            fornecedor = fornecedorRepository.findById(dto.getFornecedorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado: " + dto.getFornecedorId()));
        }

        NotaEntrada nota = new NotaEntrada();
        nota.setNumero(dto.getNumero());
        nota.setFornecedor(fornecedor);
        nota.setDataNota(dto.getDataNota());
        nota.setValorNota(dto.getValorNota());
        nota.setVencimento(dto.getVencimento());
        nota.setStatus(StatusNotaEntrada.ATIVA);

        BigDecimal somaItens = BigDecimal.ZERO;

        for (ItemNotaEntradaDTO itemDto : dto.getItens()) {
            Produto produto = produtoRepository.findById(itemDto.getProdutoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + itemDto.getProdutoId()));

            ItemNotaEntrada item = new ItemNotaEntrada();
            item.setProduto(produto);
            item.setQuantidade(itemDto.getQuantidade());
            item.setValorUnitario(itemDto.getValorUnitario());
            item.calcularSubtotal();

            nota.adicionarItem(item);
            somaItens = somaItens.add(item.getSubtotal());

            // Dirty checking do Hibernate salva essa alteração junto, sem precisar chamar save() aqui.
            produto.setEstoqueAtual(produto.getEstoqueAtual() + itemDto.getQuantidade());
        }

        validarSomaItensIgualAoValorDaNota(somaItens, dto.getValorNota());

        // Por padrão, a nota gera um único pagamento à vista com o vencimento informado na nota.
        // O usuário pode, posteriormente, editar essa parcela ou dividi-la em mais de uma.
        Pagamento pagamento = new Pagamento();
        pagamento.setNumeroFatura(nota.getNumero());
        pagamento.setNumeroParcela(1);
        pagamento.setDataEmissao(nota.getDataNota());
        pagamento.setValorAPagar(nota.getValorNota());
        pagamento.setDataVencimento(nota.getVencimento());
        pagamento.setStatus(StatusPagamento.PENDENTE);
        nota.adicionarPagamento(pagamento);

        return toDTO(notaEntradaRepository.save(nota));
    }

    /**
     * Cancela a nota, devolve a quantidade de cada item ao estoque do produto e cancela
     * todas as parcelas de pagamento, inclusive as que já estavam pagas.
     */
    public NotaEntradaDTO cancelar(Long id) {
        NotaEntrada nota = buscarEntidade(id);

        if (nota.getStatus() == StatusNotaEntrada.CANCELADA) {
            return toDTO(nota);
        }

        for (ItemNotaEntrada item : nota.getItens()) {
            Produto produto = item.getProduto();
            produto.setEstoqueAtual(produto.getEstoqueAtual() - item.getQuantidade());
        }

        for (Pagamento pagamento : nota.getPagamentos()) {
            pagamento.setStatus(StatusPagamento.CANCELADO);
        }

        nota.setStatus(StatusNotaEntrada.CANCELADA);

        return toDTO(notaEntradaRepository.save(nota));
    }

    /**
     * Garante que a soma dos subtotais dos itens bate exatamente com o valor informado da nota.
     * Compara com 2 casas decimais (arredondamento bancário) para não reprovar por causa de escala.
     */
    private void validarSomaItensIgualAoValorDaNota(BigDecimal somaItens, BigDecimal valorNota) {
        BigDecimal somaArredondada = somaItens.setScale(ESCALA_VALOR, RoundingMode.HALF_EVEN);
        BigDecimal valorNotaArredondado = valorNota.setScale(ESCALA_VALOR, RoundingMode.HALF_EVEN);

        if (somaArredondada.compareTo(valorNotaArredondado) != 0) {
            throw new DivergenciaValorNotaException(
                    "A soma dos itens (" + somaArredondada + ") não bate com o valor da nota (" + valorNotaArredondado + ").");
        }
    }

    private NotaEntrada buscarEntidade(Long id) {
        return notaEntradaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nota de entrada não encontrada: " + id));
    }

    private NotaEntradaDTO toDTO(NotaEntrada nota) {
        NotaEntradaDTO dto = new NotaEntradaDTO();
        dto.setId(nota.getId());
        dto.setNumero(nota.getNumero());
        dto.setFornecedorId(nota.getFornecedor() != null ? nota.getFornecedor().getId() : null);
        dto.setFornecedorNome(nota.getFornecedor() != null ? nota.getFornecedor().getNome() : null);
        dto.setDataNota(nota.getDataNota());
        dto.setValorNota(nota.getValorNota());
        dto.setVencimento(nota.getVencimento());
        dto.setStatus(nota.getStatus().name());
        dto.setItens(nota.getItens().stream().map(this::toItemDTO).toList());
        dto.setPagamentos(nota.getPagamentos().stream().map(this::toPagamentoDTO).toList());
        return dto;
    }

    private ItemNotaEntradaDTO toItemDTO(ItemNotaEntrada item) {
        ItemNotaEntradaDTO dto = new ItemNotaEntradaDTO();
        dto.setId(item.getId());
        dto.setProdutoId(item.getProduto().getId());
        dto.setProdutoNome(item.getProduto().getNome());
        dto.setQuantidade(item.getQuantidade());
        dto.setValorUnitario(item.getValorUnitario());
        dto.setSubtotal(item.getSubtotal());
        return dto;
    }

    private PagamentoDTO toPagamentoDTO(Pagamento pagamento) {
        PagamentoDTO dto = new PagamentoDTO();
        dto.setId(pagamento.getId());
        dto.setNotaEntradaId(pagamento.getNotaEntrada().getId());
        dto.setNumeroFatura(pagamento.getNumeroFatura());
        dto.setNumeroParcela(pagamento.getNumeroParcela());
        dto.setDataEmissao(pagamento.getDataEmissao());
        dto.setValorAPagar(pagamento.getValorAPagar());
        dto.setDataVencimento(pagamento.getDataVencimento());
        dto.setStatus(pagamento.getStatus().name());
        return dto;
    }
}

package com.sandrojam.controlevendas.service;

import com.sandrojam.controlevendas.dto.ProdutoDTO;
import com.sandrojam.controlevendas.exception.ResourceNotFoundException;
import com.sandrojam.controlevendas.model.Categoria;
import com.sandrojam.controlevendas.model.Fornecedor;
import com.sandrojam.controlevendas.model.Produto;
import com.sandrojam.controlevendas.repository.CategoriaRepository;
import com.sandrojam.controlevendas.repository.FornecedorRepository;
import com.sandrojam.controlevendas.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;
    private final FornecedorRepository fornecedorRepository;

    public ProdutoService(ProdutoRepository produtoRepository, CategoriaRepository categoriaRepository,
                           FornecedorRepository fornecedorRepository) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
        this.fornecedorRepository = fornecedorRepository;
    }

    @Transactional(readOnly = true)
    public List<ProdutoDTO> listarTodos() {
        return produtoRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Busca produtos pelo nome (contendo, case-insensitive). Se o termo vier vazio/nulo,
     * retorna todos — assim o mesmo endpoint serve tanto pra listagem quanto pra busca.
     */
    @Transactional(readOnly = true)
    public List<ProdutoDTO> buscar(String nome) {
        List<Produto> produtos = (nome == null || nome.isBlank())
                ? produtoRepository.findAll()
                : produtoRepository.findByNomeContainingIgnoreCase(nome.trim());

        return produtos.stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public ProdutoDTO buscarPorId(Long id) {
        return toDTO(buscarEntidade(id));
    }

    /** Produtos ativos cujo estoque atual já caiu para o nível mínimo configurado (ou abaixo). */
    @Transactional(readOnly = true)
    public List<ProdutoDTO> listarEstoqueBaixo() {
        return produtoRepository.findComEstoqueBaixo().stream()
                .map(this::toDTO)
                .toList();
    }

    public ProdutoDTO criar(ProdutoDTO dto) {
        Categoria categoria = buscarCategoria(dto.getCategoriaId());
        Fornecedor fornecedor = buscarFornecedor(dto.getFornecedorId());

        Produto produto = new Produto();
        aplicarDTO(produto, dto, categoria, fornecedor);

        return toDTO(produtoRepository.save(produto));
    }

    public ProdutoDTO atualizar(Long id, ProdutoDTO dto) {
        Produto produto = buscarEntidade(id);
        Categoria categoria = buscarCategoria(dto.getCategoriaId());
        Fornecedor fornecedor = buscarFornecedor(dto.getFornecedorId());

        aplicarDTO(produto, dto, categoria, fornecedor);

        return toDTO(produtoRepository.save(produto));
    }

    public void excluir(Long id) {
        Produto produto = buscarEntidade(id);
        produtoRepository.delete(produto);
    }

    private void aplicarDTO(Produto produto, ProdutoDTO dto, Categoria categoria, Fornecedor fornecedor) {
        produto.setNome(dto.getNome());
        produto.setUnidade(dto.getUnidade());
        produto.setPreco(dto.getPreco());
        produto.setPrecoCusto(dto.getPrecoCusto() != null ? dto.getPrecoCusto() : BigDecimal.ZERO);
        produto.setEstoqueAtual(dto.getEstoqueAtual());
        produto.setEstoqueMinimo(dto.getEstoqueMinimo() != null ? dto.getEstoqueMinimo() : 0);
        produto.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);
        produto.setCategoria(categoria);
        produto.setFornecedor(fornecedor);
    }

    private Produto buscarEntidade(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + id));
    }

    private Categoria buscarCategoria(Long categoriaId) {
        return categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: " + categoriaId));
    }

    /**
     * Fornecedor é opcional no cadastro de Produto — retorna null se o id não vier preenchido.
     */
    private Fornecedor buscarFornecedor(Long fornecedorId) {
        if (fornecedorId == null) {
            return null;
        }
        return fornecedorRepository.findById(fornecedorId)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado: " + fornecedorId));
    }

    private ProdutoDTO toDTO(Produto produto) {
        ProdutoDTO dto = new ProdutoDTO();
        dto.setId(produto.getId());
        dto.setNome(produto.getNome());
        dto.setUnidade(produto.getUnidade());
        dto.setPreco(produto.getPreco());
        dto.setPrecoCusto(produto.getPrecoCusto());
        dto.setEstoqueAtual(produto.getEstoqueAtual());
        dto.setEstoqueMinimo(produto.getEstoqueMinimo());
        dto.setEstoqueBaixo(produto.getEstoqueMinimo() > 0 && produto.getEstoqueAtual() <= produto.getEstoqueMinimo());
        dto.setAtivo(produto.getAtivo());
        dto.setCategoriaId(produto.getCategoria().getId());
        dto.setCategoriaNome(produto.getCategoria().getNome());
        if (produto.getFornecedor() != null) {
            dto.setFornecedorId(produto.getFornecedor().getId());
            dto.setFornecedorNome(produto.getFornecedor().getNome());
        }
        return dto;
    }
}

package com.sandrojam.controlevendas.service;

import com.sandrojam.controlevendas.dto.ProdutoDTO;
import com.sandrojam.controlevendas.exception.ResourceNotFoundException;
import com.sandrojam.controlevendas.model.Categoria;
import com.sandrojam.controlevendas.model.Produto;
import com.sandrojam.controlevendas.repository.CategoriaRepository;
import com.sandrojam.controlevendas.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProdutoService(ProdutoRepository produtoRepository, CategoriaRepository categoriaRepository) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
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

    public ProdutoDTO criar(ProdutoDTO dto) {
        Categoria categoria = buscarCategoria(dto.getCategoriaId());

        Produto produto = new Produto();
        aplicarDTO(produto, dto, categoria);

        return toDTO(produtoRepository.save(produto));
    }

    public ProdutoDTO atualizar(Long id, ProdutoDTO dto) {
        Produto produto = buscarEntidade(id);
        Categoria categoria = buscarCategoria(dto.getCategoriaId());

        aplicarDTO(produto, dto, categoria);

        return toDTO(produtoRepository.save(produto));
    }

    public void excluir(Long id) {
        Produto produto = buscarEntidade(id);
        produtoRepository.delete(produto);
    }

    private void aplicarDTO(Produto produto, ProdutoDTO dto, Categoria categoria) {
        produto.setNome(dto.getNome());
        produto.setUnidade(dto.getUnidade());
        produto.setPreco(dto.getPreco());
        produto.setEstoqueAtual(dto.getEstoqueAtual());
        produto.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);
        produto.setCategoria(categoria);
    }

    private Produto buscarEntidade(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + id));
    }

    private Categoria buscarCategoria(Long categoriaId) {
        return categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: " + categoriaId));
    }

    private ProdutoDTO toDTO(Produto produto) {
        ProdutoDTO dto = new ProdutoDTO();
        dto.setId(produto.getId());
        dto.setNome(produto.getNome());
        dto.setUnidade(produto.getUnidade());
        dto.setPreco(produto.getPreco());
        dto.setEstoqueAtual(produto.getEstoqueAtual());
        dto.setAtivo(produto.getAtivo());
        dto.setCategoriaId(produto.getCategoria().getId());
        dto.setCategoriaNome(produto.getCategoria().getNome());
        return dto;
    }
}

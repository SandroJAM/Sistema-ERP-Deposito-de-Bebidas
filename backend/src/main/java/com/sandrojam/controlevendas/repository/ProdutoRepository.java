package com.sandrojam.controlevendas.repository;

import com.sandrojam.controlevendas.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findByAtivoTrue();
    List<Produto> findByCategoriaId(Long categoriaId);
    List<Produto> findByNomeContainingIgnoreCase(String nome);

    /**
     * Produtos ativos com estoqueAtual no nível mínimo ou abaixo dele. estoqueMinimo = 0
     * significa "sem alerta configurado" pra esse produto, por isso fica de fora.
     */
    @Query("SELECT p FROM Produto p WHERE p.ativo = true AND p.estoqueMinimo > 0 "
            + "AND p.estoqueAtual <= p.estoqueMinimo ORDER BY p.nome ASC")
    List<Produto> findComEstoqueBaixo();
}

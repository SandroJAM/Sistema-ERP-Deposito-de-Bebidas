package com.sandrojam.controlevendas.repository;

import com.sandrojam.controlevendas.model.Pagamento;
import com.sandrojam.controlevendas.model.StatusPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    List<Pagamento> findByNotaEntradaId(Long notaEntradaId);

    /**
     * Busca combinando filtros opcionais (todos podem vir nulos = "todos"): fornecedor (da nota de
     * origem, quando houver, ou do próprio pagamento quando avulso), status e faixa de vencimento.
     * Usa LEFT JOIN propositalmente — um path simples (p.notaEntrada.fornecedor.id) viraria inner
     * join implícito e excluiria da busca todo pagamento avulso, mesmo sem filtro de fornecedor.
     */
    @Query("SELECT p FROM Pagamento p "
            + "LEFT JOIN p.notaEntrada n "
            + "LEFT JOIN n.fornecedor nf "
            + "LEFT JOIN p.fornecedor pf "
            + "WHERE (:fornecedorId IS NULL OR nf.id = :fornecedorId OR pf.id = :fornecedorId) AND "
            + "(:status IS NULL OR p.status = :status) AND "
            + "(:vencimentoDe IS NULL OR p.dataVencimento >= :vencimentoDe) AND "
            + "(:vencimentoAte IS NULL OR p.dataVencimento <= :vencimentoAte) "
            + "ORDER BY p.dataVencimento ASC")
    List<Pagamento> buscarComFiltros(
            @Param("fornecedorId") Long fornecedorId,
            @Param("status") StatusPagamento status,
            @Param("vencimentoDe") LocalDate vencimentoDe,
            @Param("vencimentoAte") LocalDate vencimentoAte);
}

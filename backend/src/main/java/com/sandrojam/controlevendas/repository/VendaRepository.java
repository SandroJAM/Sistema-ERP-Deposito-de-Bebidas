package com.sandrojam.controlevendas.repository;

import com.sandrojam.controlevendas.model.StatusVenda;
import com.sandrojam.controlevendas.model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VendaRepository extends JpaRepository<Venda, Long> {

    /** Todas as vendas de um cliente (qualquer status), mais recentes primeiro — base do extrato. */
    List<Venda> findByCliente_IdOrderByDataVendaDesc(Long clienteId);

    /** Vendas com cliente identificado e não canceladas — base para o resumo de devedores. */
    List<Venda> findByClienteIsNotNullAndStatusNot(StatusVenda status);

    /** Vendas não canceladas dentro de um período — base do dashboard de vendas. */
    List<Venda> findByStatusNotAndDataVendaBetween(StatusVenda status, LocalDateTime inicio, LocalDateTime fim);
}

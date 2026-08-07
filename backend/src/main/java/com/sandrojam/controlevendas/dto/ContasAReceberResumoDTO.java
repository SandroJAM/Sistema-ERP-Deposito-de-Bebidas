package com.sandrojam.controlevendas.dto;

import java.math.BigDecimal;

/** Resumo de contas a receber: dívida de clientes (fiado) em aberto na data da consulta. */
public class ContasAReceberResumoDTO {

    private BigDecimal totalDevido;
    private long quantidadeClientesDevedores;
    private long quantidadeVendasPendentes;
    private BigDecimal totalRecebidoNoPeriodo;

    public BigDecimal getTotalDevido() {
        return totalDevido;
    }

    public void setTotalDevido(BigDecimal totalDevido) {
        this.totalDevido = totalDevido;
    }

    public long getQuantidadeClientesDevedores() {
        return quantidadeClientesDevedores;
    }

    public void setQuantidadeClientesDevedores(long quantidadeClientesDevedores) {
        this.quantidadeClientesDevedores = quantidadeClientesDevedores;
    }

    public long getQuantidadeVendasPendentes() {
        return quantidadeVendasPendentes;
    }

    public void setQuantidadeVendasPendentes(long quantidadeVendasPendentes) {
        this.quantidadeVendasPendentes = quantidadeVendasPendentes;
    }

    public BigDecimal getTotalRecebidoNoPeriodo() {
        return totalRecebidoNoPeriodo;
    }

    public void setTotalRecebidoNoPeriodo(BigDecimal totalRecebidoNoPeriodo) {
        this.totalRecebidoNoPeriodo = totalRecebidoNoPeriodo;
    }
}

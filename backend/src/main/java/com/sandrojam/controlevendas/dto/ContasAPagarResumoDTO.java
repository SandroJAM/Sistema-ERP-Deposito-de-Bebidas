package com.sandrojam.controlevendas.dto;

import java.math.BigDecimal;

/**
 * Resumo de contas a pagar (Pagamentos não cancelados) na data da consulta: quanto está em
 * aberto, quanto já venceu sem pagar e quanto foi pago dentro do período informado no relatório.
 */
public class ContasAPagarResumoDTO {

    private BigDecimal totalPendente;
    private BigDecimal totalVencido;
    private BigDecimal totalAVencer;
    private BigDecimal totalPagoNoPeriodo;
    private long quantidadePendentes;
    private long quantidadeVencidas;

    public BigDecimal getTotalPendente() {
        return totalPendente;
    }

    public void setTotalPendente(BigDecimal totalPendente) {
        this.totalPendente = totalPendente;
    }

    public BigDecimal getTotalVencido() {
        return totalVencido;
    }

    public void setTotalVencido(BigDecimal totalVencido) {
        this.totalVencido = totalVencido;
    }

    public BigDecimal getTotalAVencer() {
        return totalAVencer;
    }

    public void setTotalAVencer(BigDecimal totalAVencer) {
        this.totalAVencer = totalAVencer;
    }

    public BigDecimal getTotalPagoNoPeriodo() {
        return totalPagoNoPeriodo;
    }

    public void setTotalPagoNoPeriodo(BigDecimal totalPagoNoPeriodo) {
        this.totalPagoNoPeriodo = totalPagoNoPeriodo;
    }

    public long getQuantidadePendentes() {
        return quantidadePendentes;
    }

    public void setQuantidadePendentes(long quantidadePendentes) {
        this.quantidadePendentes = quantidadePendentes;
    }

    public long getQuantidadeVencidas() {
        return quantidadeVencidas;
    }

    public void setQuantidadeVencidas(long quantidadeVencidas) {
        this.quantidadeVencidas = quantidadeVencidas;
    }
}

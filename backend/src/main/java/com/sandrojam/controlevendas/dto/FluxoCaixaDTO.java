package com.sandrojam.controlevendas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Fluxo de caixa de um período: entradas = recebimentos de vendas (fiado quitado + à vista);
 * saídas = pagamentos efetivamente pagos (por dataPagamento, não por vencimento).
 */
public class FluxoCaixaDTO {

    private LocalDate inicio;
    private LocalDate fim;
    private BigDecimal totalEntradas;
    private BigDecimal totalSaidas;
    private BigDecimal saldoPeriodo;
    private List<FluxoCaixaDiaDTO> dias;

    public LocalDate getInicio() {
        return inicio;
    }

    public void setInicio(LocalDate inicio) {
        this.inicio = inicio;
    }

    public LocalDate getFim() {
        return fim;
    }

    public void setFim(LocalDate fim) {
        this.fim = fim;
    }

    public BigDecimal getTotalEntradas() {
        return totalEntradas;
    }

    public void setTotalEntradas(BigDecimal totalEntradas) {
        this.totalEntradas = totalEntradas;
    }

    public BigDecimal getTotalSaidas() {
        return totalSaidas;
    }

    public void setTotalSaidas(BigDecimal totalSaidas) {
        this.totalSaidas = totalSaidas;
    }

    public BigDecimal getSaldoPeriodo() {
        return saldoPeriodo;
    }

    public void setSaldoPeriodo(BigDecimal saldoPeriodo) {
        this.saldoPeriodo = saldoPeriodo;
    }

    public List<FluxoCaixaDiaDTO> getDias() {
        return dias;
    }

    public void setDias(List<FluxoCaixaDiaDTO> dias) {
        this.dias = dias;
    }
}

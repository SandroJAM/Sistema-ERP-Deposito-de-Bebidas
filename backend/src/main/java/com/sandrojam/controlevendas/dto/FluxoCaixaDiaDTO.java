package com.sandrojam.controlevendas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Um ponto (dia) da série do fluxo de caixa. */
public class FluxoCaixaDiaDTO {

    private LocalDate data;
    private BigDecimal entradas;
    private BigDecimal saidas;
    private BigDecimal saldoDia;

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public BigDecimal getEntradas() {
        return entradas;
    }

    public void setEntradas(BigDecimal entradas) {
        this.entradas = entradas;
    }

    public BigDecimal getSaidas() {
        return saidas;
    }

    public void setSaidas(BigDecimal saidas) {
        this.saidas = saidas;
    }

    public BigDecimal getSaldoDia() {
        return saldoDia;
    }

    public void setSaldoDia(BigDecimal saldoDia) {
        this.saldoDia = saldoDia;
    }
}

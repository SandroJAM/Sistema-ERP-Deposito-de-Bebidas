package com.sandrojam.controlevendas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Total faturado e quantidade de vendas de um dia — um ponto do gráfico do dashboard de vendas. */
public class VendaPorDiaDTO {

    private LocalDate data;
    private Long quantidadeVendas;
    private BigDecimal valorTotal;

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Long getQuantidadeVendas() {
        return quantidadeVendas;
    }

    public void setQuantidadeVendas(Long quantidadeVendas) {
        this.quantidadeVendas = quantidadeVendas;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }
}

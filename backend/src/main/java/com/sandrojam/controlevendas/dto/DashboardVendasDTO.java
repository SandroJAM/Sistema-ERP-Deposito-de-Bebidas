package com.sandrojam.controlevendas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Resumo de vendas de um período: totais gerais, ranking de produtos mais vendidos
 * e a série diária de faturamento (para gráfico). Vendas canceladas nunca entram aqui.
 */
public class DashboardVendasDTO {

    private LocalDate inicio;
    private LocalDate fim;
    private Long quantidadeVendas;
    private BigDecimal faturamentoTotal;
    private BigDecimal ticketMedio;
    private Integer totalItensVendidos;
    private List<ProdutoMaisVendidoDTO> produtosMaisVendidos;
    private List<VendaPorDiaDTO> vendasPorDia;

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

    public Long getQuantidadeVendas() {
        return quantidadeVendas;
    }

    public void setQuantidadeVendas(Long quantidadeVendas) {
        this.quantidadeVendas = quantidadeVendas;
    }

    public BigDecimal getFaturamentoTotal() {
        return faturamentoTotal;
    }

    public void setFaturamentoTotal(BigDecimal faturamentoTotal) {
        this.faturamentoTotal = faturamentoTotal;
    }

    public BigDecimal getTicketMedio() {
        return ticketMedio;
    }

    public void setTicketMedio(BigDecimal ticketMedio) {
        this.ticketMedio = ticketMedio;
    }

    public Integer getTotalItensVendidos() {
        return totalItensVendidos;
    }

    public void setTotalItensVendidos(Integer totalItensVendidos) {
        this.totalItensVendidos = totalItensVendidos;
    }

    public List<ProdutoMaisVendidoDTO> getProdutosMaisVendidos() {
        return produtosMaisVendidos;
    }

    public void setProdutosMaisVendidos(List<ProdutoMaisVendidoDTO> produtosMaisVendidos) {
        this.produtosMaisVendidos = produtosMaisVendidos;
    }

    public List<VendaPorDiaDTO> getVendasPorDia() {
        return vendasPorDia;
    }

    public void setVendasPorDia(List<VendaPorDiaDTO> vendasPorDia) {
        this.vendasPorDia = vendasPorDia;
    }
}

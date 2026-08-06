package com.sandrojam.controlevendas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Uma linha do extrato do cliente — a "bobina": uma VENDA soma na dívida, um RECEBIMENTO
 * abate. saldoAcumulado é o saldo devedor logo após esse lançamento, na ordem cronológica.
 * Para lançamentos do tipo VENDA, "itens" traz os produtos comprados; fica vazio/nulo em
 * lançamentos do tipo RECEBIMENTO.
 */
public class LancamentoExtratoDTO {

    private String tipo; // "VENDA" ou "RECEBIMENTO"
    private LocalDate data;
    private String descricao;
    private Long vendaId;
    private BigDecimal valor;
    private BigDecimal saldoAcumulado;
    private List<ItemLancamentoDTO> itens;

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getVendaId() {
        return vendaId;
    }

    public void setVendaId(Long vendaId) {
        this.vendaId = vendaId;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getSaldoAcumulado() {
        return saldoAcumulado;
    }

    public void setSaldoAcumulado(BigDecimal saldoAcumulado) {
        this.saldoAcumulado = saldoAcumulado;
    }

    public List<ItemLancamentoDTO> getItens() {
        return itens;
    }

    public void setItens(List<ItemLancamentoDTO> itens) {
        this.itens = itens;
    }
}

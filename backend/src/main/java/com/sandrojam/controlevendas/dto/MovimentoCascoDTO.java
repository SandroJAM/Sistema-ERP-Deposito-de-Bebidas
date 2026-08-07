package com.sandrojam.controlevendas.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MovimentoCascoDTO {

    private Long id;

    @NotNull
    private Long clienteId;

    private String clienteNome;

    @NotNull
    private Long tipoCascoId;

    private String tipoCascoNome;

    // "SAIDA" ou "DEVOLUCAO"
    @NotNull
    private String tipoMovimento;

    @NotNull
    @Positive
    private Integer quantidade;

    private LocalDate data;

    private Long vendaId;

    // Somente leitura: preenchido pelo backend quando tipoMovimento = PAGO (quantidade x valorReposicao).
    private BigDecimal valorCobrado;

    private String observacao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getClienteNome() {
        return clienteNome;
    }

    public void setClienteNome(String clienteNome) {
        this.clienteNome = clienteNome;
    }

    public Long getTipoCascoId() {
        return tipoCascoId;
    }

    public void setTipoCascoId(Long tipoCascoId) {
        this.tipoCascoId = tipoCascoId;
    }

    public String getTipoCascoNome() {
        return tipoCascoNome;
    }

    public void setTipoCascoNome(String tipoCascoNome) {
        this.tipoCascoNome = tipoCascoNome;
    }

    public String getTipoMovimento() {
        return tipoMovimento;
    }

    public void setTipoMovimento(String tipoMovimento) {
        this.tipoMovimento = tipoMovimento;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Long getVendaId() {
        return vendaId;
    }

    public void setVendaId(Long vendaId) {
        this.vendaId = vendaId;
    }

    public BigDecimal getValorCobrado() {
        return valorCobrado;
    }

    public void setValorCobrado(BigDecimal valorCobrado) {
        this.valorCobrado = valorCobrado;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}

package com.sandrojam.controlevendas.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class NotaEntradaDTO {

    private Long id;

    @NotBlank
    private String numero;

    // Opcional: nem toda entrada precisa estar vinculada a um fornecedor cadastrado.
    private Long fornecedorId;

    private String fornecedorNome;

    @NotNull
    private LocalDate dataNota;

    @NotNull
    private BigDecimal valorNota;

    @NotNull
    private LocalDate vencimento;

    private String status;

    @NotEmpty
    @Valid
    private List<ItemNotaEntradaDTO> itens;

    private List<PagamentoDTO> pagamentos;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Long getFornecedorId() {
        return fornecedorId;
    }

    public void setFornecedorId(Long fornecedorId) {
        this.fornecedorId = fornecedorId;
    }

    public String getFornecedorNome() {
        return fornecedorNome;
    }

    public void setFornecedorNome(String fornecedorNome) {
        this.fornecedorNome = fornecedorNome;
    }

    public LocalDate getDataNota() {
        return dataNota;
    }

    public void setDataNota(LocalDate dataNota) {
        this.dataNota = dataNota;
    }

    public BigDecimal getValorNota() {
        return valorNota;
    }

    public void setValorNota(BigDecimal valorNota) {
        this.valorNota = valorNota;
    }

    public LocalDate getVencimento() {
        return vencimento;
    }

    public void setVencimento(LocalDate vencimento) {
        this.vencimento = vencimento;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ItemNotaEntradaDTO> getItens() {
        return itens;
    }

    public void setItens(List<ItemNotaEntradaDTO> itens) {
        this.itens = itens;
    }

    public List<PagamentoDTO> getPagamentos() {
        return pagamentos;
    }

    public void setPagamentos(List<PagamentoDTO> pagamentos) {
        this.pagamentos = pagamentos;
    }
}

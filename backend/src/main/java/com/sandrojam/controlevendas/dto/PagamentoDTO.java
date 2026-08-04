package com.sandrojam.controlevendas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PagamentoDTO {

    private Long id;

    // Informe para vincular a uma nota de entrada existente. Deixe nulo para um pagamento avulso
    // (nesse caso fornecedorId é obrigatório). Ignorado em atualizações — a origem não muda depois de criado.
    private Long notaEntradaId;

    // Obrigatório quando notaEntradaId não é informado (pagamento avulso).
    // Quando o pagamento vem de uma nota, é preenchido automaticamente com o fornecedor da nota.
    private Long fornecedorId;

    private String fornecedorNome;

    @NotBlank
    private String numeroFatura;

    @NotNull
    @Positive
    private Integer numeroParcela;

    @NotNull
    private LocalDate dataEmissao;

    @NotNull
    @Positive
    private BigDecimal valorAPagar;

    @NotNull
    private LocalDate dataVencimento;

    private String descricao;

    private String status;

    // Somente leitura: "AVULSO" ou "NOTA_ENTRADA", derivado a partir de notaEntradaId.
    private String origem;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNotaEntradaId() {
        return notaEntradaId;
    }

    public void setNotaEntradaId(Long notaEntradaId) {
        this.notaEntradaId = notaEntradaId;
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

    public String getNumeroFatura() {
        return numeroFatura;
    }

    public void setNumeroFatura(String numeroFatura) {
        this.numeroFatura = numeroFatura;
    }

    public Integer getNumeroParcela() {
        return numeroParcela;
    }

    public void setNumeroParcela(Integer numeroParcela) {
        this.numeroParcela = numeroParcela;
    }

    public LocalDate getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(LocalDate dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public BigDecimal getValorAPagar() {
        return valorAPagar;
    }

    public void setValorAPagar(BigDecimal valorAPagar) {
        this.valorAPagar = valorAPagar;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }
}

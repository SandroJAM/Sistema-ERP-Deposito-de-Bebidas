package com.sandrojam.controlevendas.dto;

/** Saldo de cascos em aberto de um cliente para um tipo de casco (saídas - devoluções). */
public class SaldoCascoDTO {

    private Long clienteId;
    private String clienteNome;
    private Long tipoCascoId;
    private String tipoCascoNome;
    private Integer quantidadeEmAberto;

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

    public Integer getQuantidadeEmAberto() {
        return quantidadeEmAberto;
    }

    public void setQuantidadeEmAberto(Integer quantidadeEmAberto) {
        this.quantidadeEmAberto = quantidadeEmAberto;
    }
}

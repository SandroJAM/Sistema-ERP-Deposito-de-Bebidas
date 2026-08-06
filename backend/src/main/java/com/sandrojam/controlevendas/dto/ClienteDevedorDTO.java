package com.sandrojam.controlevendas.dto;

import java.math.BigDecimal;

/**
 * Um registro por cliente, resumindo quanto ele deve no total — usado pela tela de
 * Consulta de Vendas (que hoje lista as vendas individualmente, uma por cliente é o que
 * o usuário vê primeiro; o detalhe fica no extrato/"bobina").
 */
public class ClienteDevedorDTO {

    private Long clienteId;
    private String clienteNome;
    private String clienteTelefone;
    private BigDecimal totalDevido;
    private long quantidadeVendasPendentes;

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

    public String getClienteTelefone() {
        return clienteTelefone;
    }

    public void setClienteTelefone(String clienteTelefone) {
        this.clienteTelefone = clienteTelefone;
    }

    public BigDecimal getTotalDevido() {
        return totalDevido;
    }

    public void setTotalDevido(BigDecimal totalDevido) {
        this.totalDevido = totalDevido;
    }

    public long getQuantidadeVendasPendentes() {
        return quantidadeVendasPendentes;
    }

    public void setQuantidadeVendasPendentes(long quantidadeVendasPendentes) {
        this.quantidadeVendasPendentes = quantidadeVendasPendentes;
    }
}

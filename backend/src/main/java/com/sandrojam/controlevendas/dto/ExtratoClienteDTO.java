package com.sandrojam.controlevendas.dto;

import java.math.BigDecimal;
import java.util.List;

public class ExtratoClienteDTO {

    private Long clienteId;
    private String clienteNome;
    private BigDecimal totalDevidoGeral;
    private List<LancamentoExtratoDTO> lancamentos;
    private String empresaNome;
    private String empresaEndereco;
    private String empresaTelefone;

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

    public BigDecimal getTotalDevidoGeral() {
        return totalDevidoGeral;
    }

    public void setTotalDevidoGeral(BigDecimal totalDevidoGeral) {
        this.totalDevidoGeral = totalDevidoGeral;
    }

    public List<LancamentoExtratoDTO> getLancamentos() {
        return lancamentos;
    }

    public void setLancamentos(List<LancamentoExtratoDTO> lancamentos) {
        this.lancamentos = lancamentos;
    }

    public String getEmpresaNome() {
        return empresaNome;
    }

    public void setEmpresaNome(String empresaNome) {
        this.empresaNome = empresaNome;
    }

    public String getEmpresaEndereco() {
        return empresaEndereco;
    }

    public void setEmpresaEndereco(String empresaEndereco) {
        this.empresaEndereco = empresaEndereco;
    }

    public String getEmpresaTelefone() {
        return empresaTelefone;
    }

    public void setEmpresaTelefone(String empresaTelefone) {
        this.empresaTelefone = empresaTelefone;
    }
}

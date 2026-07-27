package com.sandrojam.controlevendas.dto;

import jakarta.validation.constraints.NotBlank;

public class FornecedorDTO {

    private Long id;

    @NotBlank
    private String nome;

    private String cnpjCpf;

    private String telefone;

    private String email;

    private Boolean ativo;

    public FornecedorDTO() {
    }

    public FornecedorDTO(Long id, String nome, String cnpjCpf, String telefone, String email, Boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.cnpjCpf = cnpjCpf;
        this.telefone = telefone;
        this.email = email;
        this.ativo = ativo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCnpjCpf() {
        return cnpjCpf;
    }

    public void setCnpjCpf(String cnpjCpf) {
        this.cnpjCpf = cnpjCpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}

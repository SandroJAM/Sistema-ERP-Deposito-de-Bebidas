package com.sandrojam.controlevendas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UsuarioDTO {

    private Long id;

    @NotBlank
    private String nome;

    @NotBlank
    @Email
    private String email;

    // Obrigatória ao criar um usuário; opcional ao editar (em branco mantém a senha atual).
    // Sempre armazenada com hash (BCrypt), nunca em texto puro.
    private String senha;

    private String perfil;

    public UsuarioDTO() {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }
}

package com.sandrojam.controlevendas.dto;

public class LoginResponseDTO {

    private Long id;
    private String token;
    private String nome;
    private String email;
    private String perfil;

    public LoginResponseDTO(Long id, String token, String nome, String email, String perfil) {
        this.id = id;
        this.token = token;
        this.nome = nome;
        this.email = email;
        this.perfil = perfil;
    }

    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getPerfil() {
        return perfil;
    }
}

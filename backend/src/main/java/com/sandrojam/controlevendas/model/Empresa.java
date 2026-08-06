package com.sandrojam.controlevendas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Dados cadastrais da empresa (o depósito de bebidas em si). Pensado pra ter um único
 * registro — nome, endereço e telefone aparecem no topo das telas e no cabeçalho do
 * histórico/PDF de vendas.
 */
@Entity
@Table(name = "empresa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nome;

    private String endereco;

    private String telefone;
}

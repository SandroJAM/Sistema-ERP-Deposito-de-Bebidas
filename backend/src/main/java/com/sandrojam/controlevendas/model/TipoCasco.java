package com.sandrojam.controlevendas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Cadastro de um tipo de vasilhame/casco controlado à parte do produto (ex: "Caixa Skol 600ml",
 * "Barril Chopp 30L"). valorReposicao é o que se cobra do cliente caso o casco não seja devolvido.
 */
@Entity
@Table(name = "tipo_casco")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TipoCasco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nome;

    @NotNull
    @PositiveOrZero
    @Column(name = "valor_reposicao", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorReposicao = BigDecimal.ZERO;

    @Column(nullable = false)
    private Boolean ativo = true;
}

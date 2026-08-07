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

@Entity
@Table(name = "produto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nome;

    private String unidade;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    /**
     * Preço de custo (quanto pagamos ao fornecedor). Uso interno: só é exposto
     * e editável na tela de CRUD de Produto, não aparece em vendas nem relatórios.
     */
    @NotNull
    @PositiveOrZero
    @Column(name = "preco_custo", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoCusto = BigDecimal.ZERO;

    @NotNull
    @PositiveOrZero
    @Column(name = "estoque_atual", nullable = false)
    private Integer estoqueAtual = 0;

    /**
     * Nível mínimo de estoque desejado. Quando estoqueAtual cai para este valor ou abaixo,
     * o produto passa a aparecer no alerta de estoque baixo. Zero desliga o alerta pra esse produto.
     */
    @NotNull
    @PositiveOrZero
    @Column(name = "estoque_minimo", nullable = false)
    private Integer estoqueMinimo = 0;

    @Column(nullable = false)
    private Boolean ativo = true;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fornecedor_id")
    private Fornecedor fornecedor;
}

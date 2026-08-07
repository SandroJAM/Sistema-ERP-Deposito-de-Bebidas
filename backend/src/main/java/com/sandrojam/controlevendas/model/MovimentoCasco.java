package com.sandrojam.controlevendas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Um lançamento de saída (casco foi com o cliente) ou devolução (cliente trouxe de volta).
 * O saldo em aberto de um cliente para um tipo de casco é a soma das SAIDA menos as DEVOLUCAO.
 */
@Entity
@Table(name = "movimento_casco")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovimentoCasco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_casco_id", nullable = false)
    private TipoCasco tipoCasco;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimentoCasco tipoMovimento;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Integer quantidade;

    @NotNull
    @Column(nullable = false)
    private LocalDate data = LocalDate.now();

    // Referência opcional à venda que originou a saída de casco, quando aplicável.
    @Column(name = "venda_id")
    private Long vendaId;

    private String observacao;
}

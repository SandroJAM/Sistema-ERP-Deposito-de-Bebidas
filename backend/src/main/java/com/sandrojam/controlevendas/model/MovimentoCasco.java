package com.sandrojam.controlevendas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Um lançamento de saída (casco foi com o cliente), devolução física (cliente trouxe de volta),
 * ou pagamento da reposição (cliente opta por pagar em vez de devolver). O saldo em aberto de um
 * cliente para um tipo de casco é a soma das SAIDA menos as DEVOLUCAO e PAGO.
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

    // Preenchido só quando tipoMovimento = PAGO: valor cobrado do cliente por não devolver o
    // casco (quantidade x valorReposicao do tipo de casco no momento do lançamento).
    @Column(name = "valor_cobrado", precision = 10, scale = 2)
    private BigDecimal valorCobrado;

    private String observacao;
}

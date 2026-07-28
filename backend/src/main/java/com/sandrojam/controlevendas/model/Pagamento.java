package com.sandrojam.controlevendas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Parcela a pagar, gerada a partir de uma NotaEntrada. Uma mesma nota pode ter
 * várias parcelas (mesmo numeroFatura, uma linha de Pagamento por parcela).
 */
@Entity
@Table(name = "pagamento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nota_entrada_id", nullable = false)
    private NotaEntrada notaEntrada;

    // Igual ao número da NotaEntrada de origem. Repetido nas várias parcelas de uma mesma nota.
    @NotBlank
    @Column(name = "numero_fatura", nullable = false)
    private String numeroFatura;

    // Identifica a parcela dentro da fatura (1, 2, 3...) quando a nota é paga em várias vezes.
    @NotNull
    @Positive
    @Column(name = "numero_parcela", nullable = false)
    private Integer numeroParcela = 1;

    @NotNull
    @Column(name = "data_emissao", nullable = false)
    private LocalDate dataEmissao;

    @NotNull
    @Positive
    @Column(name = "valor_a_pagar", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorAPagar;

    @NotNull
    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPagamento status = StatusPagamento.PENDENTE;
}

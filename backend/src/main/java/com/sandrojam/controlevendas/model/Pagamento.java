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
 * Parcela a pagar. Pode ser gerada automaticamente a partir de uma NotaEntrada (uma mesma
 * nota pode ter várias parcelas, mesmo numeroFatura, uma linha de Pagamento por parcela) ou
 * lançada avulsa diretamente pelo usuário, sem nota de origem — nesse caso o fornecedor é
 * vinculado diretamente ao pagamento.
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

    // Ausente quando o pagamento é avulso (lançado diretamente, sem nota de entrada).
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nota_entrada_id")
    private NotaEntrada notaEntrada;

    // Usado somente em pagamentos avulsos. Quando há notaEntrada, o fornecedor é o dela.
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fornecedor_id")
    private Fornecedor fornecedor;

    // Observação livre, útil principalmente em pagamentos avulsos (ex: "Aluguel do depósito").
    @Column(name = "descricao")
    private String descricao;

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

    // Preenchida automaticamente quando o pagamento é marcado como PAGO. Base do fluxo de caixa
    // (saídas), que precisa da data em que o dinheiro efetivamente saiu, não do vencimento.
    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;
}

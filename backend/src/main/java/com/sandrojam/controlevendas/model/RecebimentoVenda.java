package com.sandrojam.controlevendas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
 * Baixa (total ou parcial) de uma venda feita a um cliente. Uma mesma venda "fiado" pode
 * acumular vários recebimentos até ficar quitada — análogo às parcelas de Pagamento, mas
 * no sentido inverso (dinheiro entrando, não saindo).
 */
@Entity
@Table(name = "recebimento_venda")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecebimentoVenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venda_id", nullable = false)
    private Venda venda;

    @NotNull
    @Column(name = "data_recebimento", nullable = false)
    private LocalDate dataRecebimento = LocalDate.now();

    @NotNull
    @Positive
    @Column(name = "valor", nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    // Observação livre, ex: "Pix", "Adiantamento parcial".
    private String observacao;
}

package com.sandrojam.controlevendas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "venda")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Opcional: venda de balcão pode não ter cliente identificado.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Column(name = "data_venda", nullable = false)
    private LocalDateTime dataVenda = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusVenda status = StatusVenda.ABERTA;

    @Column(name = "valor_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    // Situação de quitação perante o cliente — não confundir com "status" (ciclo de vida da venda).
    @Enumerated(EnumType.STRING)
    @Column(name = "status_pagamento", nullable = false)
    private StatusPagamentoVenda statusPagamento = StatusPagamentoVenda.PENDENTE;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemVenda> itens = new ArrayList<>();

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecebimentoVenda> recebimentos = new ArrayList<>();

    public void adicionarItem(ItemVenda item) {
        itens.add(item);
        item.setVenda(this);
    }

    /** Soma de tudo que já foi recebido para esta venda. */
    public BigDecimal getValorPago() {
        return recebimentos.stream()
                .map(RecebimentoVenda::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Quanto ainda falta o cliente pagar. Vendas canceladas não geram dívida. */
    public BigDecimal getValorDevido() {
        if (status == StatusVenda.CANCELADA) {
            return BigDecimal.ZERO;
        }
        BigDecimal devido = valorTotal.subtract(getValorPago());
        return devido.max(BigDecimal.ZERO);
    }
}

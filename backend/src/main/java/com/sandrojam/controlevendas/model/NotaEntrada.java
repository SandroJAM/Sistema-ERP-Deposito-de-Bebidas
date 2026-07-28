package com.sandrojam.controlevendas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Nota fiscal de entrada de mercadorias. Ao ser inserida, alimenta o estoque
 * de cada Produto envolvido e gera automaticamente o(s) Pagamento(s) correspondente(s).
 */
@Entity
@Table(name = "nota_entrada")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotaEntrada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "numero", nullable = false)
    private String numero;

    // Opcional: nem toda entrada precisa estar vinculada a um fornecedor cadastrado.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fornecedor_id")
    private Fornecedor fornecedor;

    @NotNull
    @Column(name = "data_nota", nullable = false)
    private LocalDate dataNota;

    @NotNull
    @Column(name = "valor_nota", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorNota;

    @NotNull
    @Column(name = "vencimento", nullable = false)
    private LocalDate vencimento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusNotaEntrada status = StatusNotaEntrada.ATIVA;

    @OneToMany(mappedBy = "notaEntrada", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemNotaEntrada> itens = new ArrayList<>();

    @OneToMany(mappedBy = "notaEntrada", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pagamento> pagamentos = new ArrayList<>();

    public void adicionarItem(ItemNotaEntrada item) {
        itens.add(item);
        item.setNotaEntrada(this);
    }

    public void adicionarPagamento(Pagamento pagamento) {
        pagamentos.add(pagamento);
        pagamento.setNotaEntrada(this);
    }
}

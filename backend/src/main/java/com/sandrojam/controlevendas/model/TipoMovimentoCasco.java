package com.sandrojam.controlevendas.model;

public enum TipoMovimentoCasco {
    /** Casco saiu com o cliente (ex: venda de bebida em garrafa/casco retornável, ou empréstimo avulso). */
    SAIDA,
    /** Cliente devolveu o casco (baixa do saldo em aberto). */
    DEVOLUCAO
}

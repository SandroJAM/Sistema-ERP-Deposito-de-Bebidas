package com.sandrojam.controlevendas.model;

public enum TipoMovimentoCasco {
    /** Casco saiu com o cliente (ex: venda de bebida em garrafa/casco retornável, ou empréstimo avulso). */
    SAIDA,
    /** Cliente devolveu o casco (baixa do saldo em aberto). */
    DEVOLUCAO,
    /**
     * Cliente optou por pagar o valor de reposição em vez de devolver o casco — baixa o saldo em
     * aberto igual a uma devolução, mas gera um valor cobrado (entrada financeira).
     */
    PAGO
}

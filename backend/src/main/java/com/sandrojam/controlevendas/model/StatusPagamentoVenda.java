package com.sandrojam.controlevendas.model;

/**
 * Situação de quitação de uma venda perante o cliente — independente do status da venda em si
 * (ABERTA/FINALIZADA/CANCELADA). Toda venda finalizada nasce PENDENTE e só é considerada quitada
 * quando a soma dos recebimentos atinge o valor total.
 */
public enum StatusPagamentoVenda {
    PENDENTE,
    PARCIAL,
    PAGO
}

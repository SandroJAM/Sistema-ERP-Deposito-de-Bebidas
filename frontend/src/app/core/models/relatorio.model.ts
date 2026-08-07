export interface ProdutoMaisVendido {
  produtoId: number;
  produtoNome: string;
  quantidadeVendida: number;
  valorTotal: number;
}

export interface VendaPorDia {
  data: string;
  quantidadeVendas: number;
  valorTotal: number;
}

export interface DashboardVendas {
  inicio: string;
  fim: string;
  quantidadeVendas: number;
  faturamentoTotal: number;
  ticketMedio: number;
  totalItensVendidos: number;
  produtosMaisVendidos: ProdutoMaisVendido[];
  vendasPorDia: VendaPorDia[];
}

export interface ContasAPagarResumo {
  totalPendente: number;
  totalVencido: number;
  totalAVencer: number;
  totalPagoNoPeriodo: number;
  quantidadePendentes: number;
  quantidadeVencidas: number;
}

export interface ContasAReceberResumo {
  totalDevido: number;
  quantidadeClientesDevedores: number;
  quantidadeVendasPendentes: number;
  totalRecebidoNoPeriodo: number;
}

export interface FluxoCaixaDia {
  data: string;
  entradas: number;
  saidas: number;
  saldoDia: number;
}

export interface FluxoCaixa {
  inicio: string;
  fim: string;
  totalEntradas: number;
  totalSaidas: number;
  saldoPeriodo: number;
  dias: FluxoCaixaDia[];
}

export interface RelatorioFinanceiro {
  contasAPagar: ContasAPagarResumo;
  contasAReceber: ContasAReceberResumo;
  fluxoCaixa: FluxoCaixa;
}

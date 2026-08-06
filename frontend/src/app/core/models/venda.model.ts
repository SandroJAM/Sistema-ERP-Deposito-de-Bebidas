export interface ItemVendaInput {
  produtoId: number;
  quantidade: number;
}

export interface ItemVendaResultado {
  id: number;
  produtoId: number;
  produtoNome: string;
  quantidade: number;
  precoUnitario: number;
  subtotal: number;
}

export interface VendaInput {
  usuarioId: number;
  clienteId?: number | null;
  itens: ItemVendaInput[];
}

export type StatusPagamentoVenda = 'PENDENTE' | 'PARCIAL' | 'PAGO';

export interface Venda {
  id: number;
  usuarioId: number;
  clienteId: number | null;
  dataVenda: string;
  status: 'ABERTA' | 'FINALIZADA' | 'CANCELADA';
  valorTotal: number;
  statusPagamento: StatusPagamentoVenda;
  valorPago: number;
  valorDevido: number;
  itens: ItemVendaResultado[];
}

export interface ClienteDevedor {
  clienteId: number;
  clienteNome: string;
  clienteTelefone: string | null;
  totalDevido: number;
  quantidadeVendasPendentes: number;
}

export interface ItemLancamento {
  produtoNome: string;
  quantidade: number;
  precoUnitario: number;
  subtotal: number;
}

export interface LancamentoExtrato {
  tipo: 'VENDA' | 'RECEBIMENTO';
  data: string;
  descricao: string;
  vendaId: number;
  valor: number;
  saldoAcumulado: number;
  itens: ItemLancamento[] | null;
}

export interface ExtratoCliente {
  clienteId: number;
  clienteNome: string;
  totalDevidoGeral: number;
  lancamentos: LancamentoExtrato[];
  empresaNome: string | null;
  empresaEndereco: string | null;
  empresaTelefone: string | null;
}

export interface RecebimentoVendaInput {
  dataRecebimento?: string | null;
  valor: number;
  observacao?: string | null;
}

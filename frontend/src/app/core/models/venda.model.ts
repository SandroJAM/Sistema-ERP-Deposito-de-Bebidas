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

export interface Venda {
  id: number;
  usuarioId: number;
  clienteId: number | null;
  dataVenda: string;
  status: 'ABERTA' | 'FINALIZADA' | 'CANCELADA';
  valorTotal: number;
  itens: ItemVendaResultado[];
}

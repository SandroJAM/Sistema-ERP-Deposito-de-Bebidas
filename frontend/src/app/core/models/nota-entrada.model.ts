export interface ItemNotaEntradaInput {
  produtoId: number;
  quantidade: number;
  valorUnitario: number;
}

export interface ItemNotaEntradaResultado {
  id: number;
  produtoId: number;
  produtoNome: string;
  quantidade: number;
  valorUnitario: number;
  subtotal: number;
}

export interface PagamentoResultado {
  id: number;
  notaEntradaId: number | null;
  fornecedorId: number | null;
  fornecedorNome: string | null;
  numeroFatura: string;
  numeroParcela: number;
  dataEmissao: string;
  valorAPagar: number;
  dataVencimento: string;
  descricao: string | null;
  status: 'PENDENTE' | 'PAGO' | 'CANCELADO';
  origem: 'AVULSO' | 'NOTA_ENTRADA';
}

export interface NotaEntradaInput {
  numero: string;
  fornecedorId?: number | null;
  dataNota: string;
  valorNota: number;
  vencimento: string;
  numeroParcelas?: number;
  itens: ItemNotaEntradaInput[];
}

export interface NotaEntrada {
  id: number;
  numero: string;
  fornecedorId: number | null;
  fornecedorNome: string | null;
  dataNota: string;
  valorNota: number;
  vencimento: string;
  status: 'ATIVA' | 'CANCELADA';
  itens: ItemNotaEntradaResultado[];
  pagamentos: PagamentoResultado[];
}

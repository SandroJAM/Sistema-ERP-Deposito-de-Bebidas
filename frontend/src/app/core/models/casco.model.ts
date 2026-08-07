export interface TipoCasco {
  id: number;
  nome: string;
  valorReposicao: number;
  ativo: boolean;
}

export type TipoCascoForm = Omit<TipoCasco, 'id'>;

export type TipoMovimentoCasco = 'SAIDA' | 'DEVOLUCAO' | 'PAGO';

export interface MovimentoCasco {
  id: number;
  clienteId: number;
  clienteNome: string;
  tipoCascoId: number;
  tipoCascoNome: string;
  tipoMovimento: TipoMovimentoCasco;
  quantidade: number;
  data: string;
  vendaId: number | null;
  /** Somente leitura: preenchido pelo backend quando tipoMovimento = 'PAGO'. */
  valorCobrado: number | null;
  observacao: string | null;
}

export interface MovimentoCascoForm {
  clienteId: number;
  tipoCascoId: number;
  tipoMovimento: TipoMovimentoCasco;
  quantidade: number;
  data?: string | null;
  vendaId?: number | null;
  observacao?: string | null;
}

export interface SaldoCasco {
  clienteId: number;
  clienteNome: string;
  tipoCascoId: number;
  tipoCascoNome: string;
  quantidadeEmAberto: number;
}

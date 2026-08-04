export type { PagamentoResultado as Pagamento } from './nota-entrada.model';

/**
 * Payload de criação/edição. notaEntradaId só é usado ao criar (vincula a uma nota existente,
 * ex: dividir a parcela original em mais de uma) — em pagamentos avulsos, deixe nulo/omitido e
 * informe fornecedorId. Ignorado em edição, já que a origem não muda depois de criado.
 */
export interface PagamentoForm {
  notaEntradaId?: number | null;
  fornecedorId: number | null;
  numeroFatura: string;
  numeroParcela: number;
  dataEmissao: string;
  valorAPagar: number;
  dataVencimento: string;
  descricao: string | null;
  status: 'PENDENTE' | 'PAGO' | 'CANCELADO';
}

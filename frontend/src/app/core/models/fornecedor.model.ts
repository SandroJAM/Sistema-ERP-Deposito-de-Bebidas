export interface Fornecedor {
  id: number;
  nome: string;
  cnpjCpf: string | null;
  telefone: string | null;
  email: string | null;
  ativo: boolean;
}

export type FornecedorForm = Omit<Fornecedor, 'id'>;

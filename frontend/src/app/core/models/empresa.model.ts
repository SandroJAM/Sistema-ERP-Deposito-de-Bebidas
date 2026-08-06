export interface Empresa {
  id: number;
  nome: string;
  endereco: string | null;
  telefone: string | null;
}

export type EmpresaForm = Omit<Empresa, 'id'>;

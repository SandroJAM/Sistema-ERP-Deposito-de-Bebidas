export interface Cliente {
  id: number;
  nome: string;
  telefone: string | null;
}

export type ClienteForm = Omit<Cliente, 'id'>;

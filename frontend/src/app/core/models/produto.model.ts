export interface Produto {
  id: number;
  nome: string;
  unidade: string | null;
  preco: number;
  estoqueAtual: number;
  ativo: boolean;
  categoriaId: number;
  categoriaNome: string;
}

export type ProdutoForm = Omit<Produto, 'id' | 'categoriaNome'>;

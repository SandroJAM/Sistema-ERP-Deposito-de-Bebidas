export interface Produto {
  id: number;
  nome: string;
  unidade: string | null;
  preco: number;
  /** Preço de custo — uso interno, editável apenas na tela de Produtos. */
  precoCusto: number;
  estoqueAtual: number;
  ativo: boolean;
  categoriaId: number;
  categoriaNome: string;
  fornecedorId: number | null;
  fornecedorNome: string | null;
}

export type ProdutoForm = Omit<Produto, 'id' | 'categoriaNome' | 'fornecedorNome'>;

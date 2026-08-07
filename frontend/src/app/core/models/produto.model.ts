export interface Produto {
  id: number;
  nome: string;
  unidade: string | null;
  preco: number;
  /** Preço de custo — uso interno, editável apenas na tela de Produtos. */
  precoCusto: number;
  estoqueAtual: number;
  /** Nível mínimo desejado de estoque. Zero = alerta desligado para esse produto. */
  estoqueMinimo: number;
  /** Somente leitura: true quando estoqueAtual <= estoqueMinimo (e estoqueMinimo > 0). */
  estoqueBaixo: boolean;
  ativo: boolean;
  categoriaId: number;
  categoriaNome: string;
  fornecedorId: number | null;
  fornecedorNome: string | null;
}

export type ProdutoForm = Omit<Produto, 'id' | 'categoriaNome' | 'fornecedorNome' | 'estoqueBaixo'>;

export interface Categoria {
  id: number;
  nome: string;
}

export type CategoriaForm = Omit<Categoria, 'id'>;

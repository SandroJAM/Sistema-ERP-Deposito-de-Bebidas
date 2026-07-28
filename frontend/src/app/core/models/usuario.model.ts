export interface Usuario {
  id: number;
  nome: string;
  email: string;
  perfil: 'ADMIN' | 'VENDEDOR';
}

export interface UsuarioForm {
  nome: string;
  email: string;
  perfil: 'ADMIN' | 'VENDEDOR';
  // Obrigatória ao criar; deixe em branco/omita ao editar para manter a senha atual.
  senha?: string;
}

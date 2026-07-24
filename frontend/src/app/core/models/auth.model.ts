export interface LoginRequest {
  email: string;
  senha: string;
}

export interface LoginResponse {
  id: number;
  token: string;
  nome: string;
  email: string;
  perfil: string;
}

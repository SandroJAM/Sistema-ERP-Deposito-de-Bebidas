import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LoginRequest, LoginResponse } from '../models/auth.model';

const CHAVE_TOKEN = 'cvb_token';
const CHAVE_USUARIO = 'cvb_usuario';

@Injectable({ providedIn: 'root' })
export class AuthService {
  // Signal com o usuário logado (ou null se não houver ninguém).
  // Componentes podem ler isso reativamente, ex: no template com usuarioLogado().
  usuarioLogado = signal<Omit<LoginResponse, 'token'> | null>(this.carregarUsuarioSalvo());

  constructor(private http: HttpClient, private router: Router) {}

  login(dados: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${environment.apiUrl}/auth/login`, dados).pipe(
      tap((resposta) => {
        localStorage.setItem(CHAVE_TOKEN, resposta.token);
        const usuario = { id: resposta.id, nome: resposta.nome, email: resposta.email, perfil: resposta.perfil };
        localStorage.setItem(CHAVE_USUARIO, JSON.stringify(usuario));
        this.usuarioLogado.set(usuario);
      })
    );
  }

  logout(): void {
    localStorage.removeItem(CHAVE_TOKEN);
    localStorage.removeItem(CHAVE_USUARIO);
    this.usuarioLogado.set(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(CHAVE_TOKEN);
  }

  estaAutenticado(): boolean {
    return !!this.getToken();
  }

  private carregarUsuarioSalvo(): Omit<LoginResponse, 'token'> | null {
    const bruto = localStorage.getItem(CHAVE_USUARIO);
    return bruto ? JSON.parse(bruto) : null;
  }
}

import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Produto, ProdutoForm } from '../models/produto.model';

@Injectable({ providedIn: 'root' })
export class ProdutoService {
  private http = inject(HttpClient);
  private url = `${environment.apiUrl}/produtos`;

  listar(nome?: string): Observable<Produto[]> {
    const params = nome ? new HttpParams().set('nome', nome) : undefined;
    return this.http.get<Produto[]>(this.url, { params });
  }

  criar(dados: ProdutoForm): Observable<Produto> {
    return this.http.post<Produto>(this.url, dados);
  }

  atualizar(id: number, dados: ProdutoForm): Observable<Produto> {
    return this.http.put<Produto>(`${this.url}/${id}`, dados);
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}

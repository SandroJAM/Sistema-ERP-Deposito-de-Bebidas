import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Fornecedor, FornecedorForm } from '../models/fornecedor.model';

@Injectable({ providedIn: 'root' })
export class FornecedorService {
  private http = inject(HttpClient);
  private url = `${environment.apiUrl}/fornecedores`;

  listar(nome?: string): Observable<Fornecedor[]> {
    const params = nome ? new HttpParams().set('nome', nome) : undefined;
    return this.http.get<Fornecedor[]>(this.url, { params });
  }

  criar(dados: FornecedorForm): Observable<Fornecedor> {
    return this.http.post<Fornecedor>(this.url, dados);
  }

  atualizar(id: number, dados: FornecedorForm): Observable<Fornecedor> {
    return this.http.put<Fornecedor>(`${this.url}/${id}`, dados);
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}

import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { Empresa, EmpresaForm } from '../models/empresa.model';

@Injectable({ providedIn: 'root' })
export class EmpresaService {
  private http = inject(HttpClient);
  private url = `${environment.apiUrl}/empresas`;

  listar(): Observable<Empresa[]> {
    return this.http.get<Empresa[]>(this.url);
  }

  /** A empresa cadastrada (normalmente só uma) — usada no cabeçalho das telas. Null se não houver nenhuma. */
  buscarAtual(): Observable<Empresa | null> {
    return this.http.get<Empresa>(`${this.url}/atual`).pipe(catchError(() => of(null)));
  }

  criar(dados: EmpresaForm): Observable<Empresa> {
    return this.http.post<Empresa>(this.url, dados);
  }

  atualizar(id: number, dados: EmpresaForm): Observable<Empresa> {
    return this.http.put<Empresa>(`${this.url}/${id}`, dados);
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}

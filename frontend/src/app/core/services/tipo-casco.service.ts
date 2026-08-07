import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { TipoCasco, TipoCascoForm } from '../models/casco.model';

@Injectable({ providedIn: 'root' })
export class TipoCascoService {
  private http = inject(HttpClient);
  private url = `${environment.apiUrl}/tipos-casco`;

  listar(somenteAtivos = false): Observable<TipoCasco[]> {
    const params = new HttpParams().set('somenteAtivos', somenteAtivos);
    return this.http.get<TipoCasco[]>(this.url, { params });
  }

  criar(dados: TipoCascoForm): Observable<TipoCasco> {
    return this.http.post<TipoCasco>(this.url, dados);
  }

  atualizar(id: number, dados: TipoCascoForm): Observable<TipoCasco> {
    return this.http.put<TipoCasco>(`${this.url}/${id}`, dados);
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}

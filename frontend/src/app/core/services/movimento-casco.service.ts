import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { MovimentoCasco, MovimentoCascoForm, SaldoCasco } from '../models/casco.model';

@Injectable({ providedIn: 'root' })
export class MovimentoCascoService {
  private http = inject(HttpClient);
  private url = `${environment.apiUrl}/movimentos-casco`;

  listar(clienteId?: number | null): Observable<MovimentoCasco[]> {
    const params = clienteId ? new HttpParams().set('clienteId', clienteId) : undefined;
    return this.http.get<MovimentoCasco[]>(this.url, { params });
  }

  listarSaldos(): Observable<SaldoCasco[]> {
    return this.http.get<SaldoCasco[]>(`${this.url}/saldos`);
  }

  registrar(dados: MovimentoCascoForm): Observable<MovimentoCasco> {
    return this.http.post<MovimentoCasco>(this.url, dados);
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}

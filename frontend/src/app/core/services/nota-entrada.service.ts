import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { NotaEntrada, NotaEntradaInput } from '../models/nota-entrada.model';

@Injectable({ providedIn: 'root' })
export class NotaEntradaService {
  private http = inject(HttpClient);
  private url = `${environment.apiUrl}/notas-entrada`;

  listar(): Observable<NotaEntrada[]> {
    return this.http.get<NotaEntrada[]>(this.url);
  }

  buscarPorId(id: number): Observable<NotaEntrada> {
    return this.http.get<NotaEntrada>(`${this.url}/${id}`);
  }

  criar(dados: NotaEntradaInput): Observable<NotaEntrada> {
    return this.http.post<NotaEntrada>(this.url, dados);
  }

  cancelar(id: number): Observable<NotaEntrada> {
    return this.http.post<NotaEntrada>(`${this.url}/${id}/cancelar`, {});
  }
}

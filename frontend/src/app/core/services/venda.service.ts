import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Venda, VendaInput } from '../models/venda.model';

@Injectable({ providedIn: 'root' })
export class VendaService {
  private http = inject(HttpClient);
  private url = `${environment.apiUrl}/vendas`;

  listar(): Observable<Venda[]> {
    return this.http.get<Venda[]>(this.url);
  }

  criar(dados: VendaInput): Observable<Venda> {
    return this.http.post<Venda>(this.url, dados);
  }

  cancelar(id: number): Observable<Venda> {
    return this.http.post<Venda>(`${this.url}/${id}/cancelar`, {});
  }
}

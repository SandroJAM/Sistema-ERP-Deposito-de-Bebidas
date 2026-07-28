import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Pagamento } from '../models/pagamento.model';

export interface FiltrosPagamento {
  notaEntradaId?: number | null;
  fornecedorId?: number | null;
  status?: 'PENDENTE' | 'PAGO' | 'CANCELADO' | null;
  vencimentoDe?: string | null;
  vencimentoAte?: string | null;
}

@Injectable({ providedIn: 'root' })
export class PagamentoService {
  private http = inject(HttpClient);
  private url = `${environment.apiUrl}/pagamentos`;

  listar(filtros: FiltrosPagamento = {}): Observable<Pagamento[]> {
    let params = new HttpParams();

    if (filtros.notaEntradaId != null) {
      params = params.set('notaEntradaId', filtros.notaEntradaId);
    }
    if (filtros.fornecedorId != null) {
      params = params.set('fornecedorId', filtros.fornecedorId);
    }
    if (filtros.status) {
      params = params.set('status', filtros.status);
    }
    if (filtros.vencimentoDe) {
      params = params.set('vencimentoDe', filtros.vencimentoDe);
    }
    if (filtros.vencimentoAte) {
      params = params.set('vencimentoAte', filtros.vencimentoAte);
    }

    return this.http.get<Pagamento[]>(this.url, { params });
  }

  marcarComoPago(id: number): Observable<Pagamento> {
    return this.http.post<Pagamento>(`${this.url}/${id}/marcar-pago`, {});
  }
}

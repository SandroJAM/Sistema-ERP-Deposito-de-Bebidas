import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { DashboardVendas, RelatorioFinanceiro } from '../models/relatorio.model';

@Injectable({ providedIn: 'root' })
export class RelatorioService {
  private http = inject(HttpClient);
  private url = `${environment.apiUrl}/relatorios`;

  dashboardVendas(inicio?: string | null, fim?: string | null): Observable<DashboardVendas> {
    return this.http.get<DashboardVendas>(`${this.url}/vendas`, { params: this.montarParams(inicio, fim) });
  }

  financeiro(inicio?: string | null, fim?: string | null): Observable<RelatorioFinanceiro> {
    return this.http.get<RelatorioFinanceiro>(`${this.url}/financeiro`, { params: this.montarParams(inicio, fim) });
  }

  private montarParams(inicio?: string | null, fim?: string | null): HttpParams {
    let params = new HttpParams();
    if (inicio) {
      params = params.set('inicio', inicio);
    }
    if (fim) {
      params = params.set('fim', fim);
    }
    return params;
  }
}

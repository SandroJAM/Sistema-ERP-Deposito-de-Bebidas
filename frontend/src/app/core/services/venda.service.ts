import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  ClienteDevedor,
  ExtratoCliente,
  RecebimentoVendaInput,
  StatusPagamentoVenda,
  Venda,
  VendaInput,
} from '../models/venda.model';

export interface FiltrosExtratoCliente {
  inicio?: string | null;
  fim?: string | null;
  somenteDevido?: boolean;
  statusPagamento?: StatusPagamentoVenda | null;
}

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

  listarDevedores(): Observable<ClienteDevedor[]> {
    return this.http.get<ClienteDevedor[]>(`${this.url}/clientes/devedores`);
  }

  buscarExtratoCliente(clienteId: number, filtros: FiltrosExtratoCliente = {}): Observable<ExtratoCliente> {
    return this.http.get<ExtratoCliente>(`${this.url}/clientes/${clienteId}/extrato`, {
      params: this.construirParams(filtros),
    });
  }

  registrarRecebimento(vendaId: number, dados: RecebimentoVendaInput): Observable<Venda> {
    return this.http.post<Venda>(`${this.url}/${vendaId}/recebimentos`, dados);
  }

  baixarExtratoPdf(clienteId: number, filtros: FiltrosExtratoCliente = {}): Observable<Blob> {
    return this.http.get(`${this.url}/clientes/${clienteId}/extrato/pdf`, {
      params: this.construirParams(filtros),
      responseType: 'blob',
    });
  }

  private construirParams(filtros: FiltrosExtratoCliente): HttpParams {
    let params = new HttpParams();

    if (filtros.inicio) {
      params = params.set('inicio', filtros.inicio);
    }
    if (filtros.fim) {
      params = params.set('fim', filtros.fim);
    }
    if (filtros.somenteDevido != null) {
      params = params.set('somenteDevido', filtros.somenteDevido);
    }
    if (filtros.statusPagamento) {
      params = params.set('statusPagamento', filtros.statusPagamento);
    }

    return params;
  }
}

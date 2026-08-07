import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RelatorioService } from '../../../core/services/relatorio.service';
import { DashboardVendas } from '../../../core/models/relatorio.model';

@Component({
  selector: 'app-dashboard-vendas',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard-vendas.component.html',
})
export class DashboardVendasComponent implements OnInit {
  private relatorioService = inject(RelatorioService);

  carregando = signal(true);
  erro = signal<string | null>(null);
  dashboard = signal<DashboardVendas | null>(null);

  // Nulos = deixa o backend decidir (últimos 30 dias); os inputs são preenchidos
  // com o período que voltou na resposta, pra o usuário ver o que está sendo exibido.
  periodoInicio = signal<string | null>(null);
  periodoFim = signal<string | null>(null);

  // Maior valor diário do período — usado só pra calcular a altura das barras do gráfico.
  maiorValorDia = computed(() => {
    const dias = this.dashboard()?.vendasPorDia ?? [];
    return dias.reduce((maior, dia) => Math.max(maior, dia.valorTotal), 0);
  });

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.carregando.set(true);
    this.erro.set(null);

    this.relatorioService.dashboardVendas(this.periodoInicio(), this.periodoFim()).subscribe({
      next: (dados) => {
        this.dashboard.set(dados);
        this.periodoInicio.set(dados.inicio);
        this.periodoFim.set(dados.fim);
        this.carregando.set(false);
      },
      error: () => {
        this.erro.set('Não foi possível carregar o dashboard de vendas.');
        this.carregando.set(false);
      },
    });
  }

  aplicarPeriodo(): void {
    this.carregar();
  }

  usarUltimos7Dias(): void {
    this.definirJanela(6);
  }

  usarUltimos30Dias(): void {
    this.definirJanela(29);
  }

  usarMesAtual(): void {
    const hoje = new Date();
    const inicioMes = new Date(hoje.getFullYear(), hoje.getMonth(), 1);
    this.periodoInicio.set(this.paraISO(inicioMes));
    this.periodoFim.set(this.paraISO(hoje));
    this.carregar();
  }

  /** Altura em % da barra do gráfico, proporcional ao maior valor do período (mínimo de 4% pra ficar visível). */
  alturaBarra(valor: number): number {
    const maior = this.maiorValorDia();
    if (maior <= 0) {
      return 0;
    }
    return Math.max((valor / maior) * 100, valor > 0 ? 4 : 0);
  }

  private definirJanela(diasAtras: number): void {
    const hoje = new Date();
    const inicio = new Date(hoje);
    inicio.setDate(inicio.getDate() - diasAtras);
    this.periodoInicio.set(this.paraISO(inicio));
    this.periodoFim.set(this.paraISO(hoje));
    this.carregar();
  }

  private paraISO(data: Date): string {
    return data.toISOString().slice(0, 10);
  }
}

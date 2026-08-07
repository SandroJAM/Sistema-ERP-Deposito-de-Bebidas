import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RelatorioService } from '../../../core/services/relatorio.service';
import { RelatorioFinanceiro } from '../../../core/models/relatorio.model';

@Component({
  selector: 'app-financeiro',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './financeiro.component.html',
})
export class FinanceiroComponent implements OnInit {
  private relatorioService = inject(RelatorioService);

  carregando = signal(true);
  erro = signal<string | null>(null);
  relatorio = signal<RelatorioFinanceiro | null>(null);

  periodoInicio = signal<string | null>(null);
  periodoFim = signal<string | null>(null);

  // Maior movimento diário (entrada ou saída) — usado só pra escalar a altura das barras.
  maiorMovimentoDia = computed(() => {
    const dias = this.relatorio()?.fluxoCaixa.dias ?? [];
    return dias.reduce((maior, dia) => Math.max(maior, dia.entradas, dia.saidas), 0);
  });

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.carregando.set(true);
    this.erro.set(null);

    this.relatorioService.financeiro(this.periodoInicio(), this.periodoFim()).subscribe({
      next: (dados) => {
        this.relatorio.set(dados);
        this.periodoInicio.set(dados.fluxoCaixa.inicio);
        this.periodoFim.set(dados.fluxoCaixa.fim);
        this.carregando.set(false);
      },
      error: () => {
        this.erro.set('Não foi possível carregar o relatório financeiro.');
        this.carregando.set(false);
      },
    });
  }

  aplicarPeriodo(): void {
    this.carregar();
  }

  usarUltimos30Dias(): void {
    const hoje = new Date();
    const inicio = new Date(hoje);
    inicio.setDate(inicio.getDate() - 29);
    this.periodoInicio.set(this.paraISO(inicio));
    this.periodoFim.set(this.paraISO(hoje));
    this.carregar();
  }

  usarMesAtual(): void {
    const hoje = new Date();
    const inicioMes = new Date(hoje.getFullYear(), hoje.getMonth(), 1);
    this.periodoInicio.set(this.paraISO(inicioMes));
    this.periodoFim.set(this.paraISO(hoje));
    this.carregar();
  }

  alturaBarra(valor: number): number {
    const maior = this.maiorMovimentoDia();
    if (maior <= 0) {
      return 0;
    }
    return Math.max((valor / maior) * 100, valor > 0 ? 4 : 0);
  }

  private paraISO(data: Date): string {
    return data.toISOString().slice(0, 10);
  }
}

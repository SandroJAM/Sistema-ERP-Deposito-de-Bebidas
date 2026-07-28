import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PagamentoService } from '../../../core/services/pagamento.service';
import { FornecedorService } from '../../../core/services/fornecedor.service';
import { Pagamento } from '../../../core/models/pagamento.model';
import { Fornecedor } from '../../../core/models/fornecedor.model';

type StatusFiltro = 'PENDENTE' | 'PAGO' | 'CANCELADO' | null;

@Component({
  selector: 'app-consulta-pagamentos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './consulta-pagamentos.component.html',
})
export class ConsultaPagamentosComponent implements OnInit {
  private pagamentoService = inject(PagamentoService);
  private fornecedorService = inject(FornecedorService);

  fornecedores = signal<Fornecedor[]>([]);

  // ----- Filtros -----
  fornecedorSelecionadoId = signal<number | null>(null);
  statusSelecionado = signal<StatusFiltro>(null);
  vencimentoDe = signal<string | null>(null);
  vencimentoAte = signal<string | null>(null);

  pagamentos = signal<Pagamento[]>([]);
  carregando = signal(true);

  ngOnInit(): void {
    this.fornecedorService.listar().subscribe((fornecedores) => this.fornecedores.set(fornecedores));
    this.carregarPagamentos();
  }

  aoMudarFiltro(): void {
    this.carregarPagamentos();
  }

  limparFiltros(): void {
    this.fornecedorSelecionadoId.set(null);
    this.statusSelecionado.set(null);
    this.vencimentoDe.set(null);
    this.vencimentoAte.set(null);
    this.carregarPagamentos();
  }

  carregarPagamentos(): void {
    this.carregando.set(true);
    this.pagamentoService
      .listar({
        fornecedorId: this.fornecedorSelecionadoId(),
        status: this.statusSelecionado(),
        vencimentoDe: this.vencimentoDe(),
        vencimentoAte: this.vencimentoAte(),
      })
      .subscribe({
        next: (pagamentos) => {
          this.pagamentos.set(pagamentos);
          this.carregando.set(false);
        },
        error: () => this.carregando.set(false),
      });
  }

  marcarComoPago(pagamento: Pagamento): void {
    this.pagamentoService.marcarComoPago(pagamento.id).subscribe({
      next: () => this.carregarPagamentos(),
      error: () => alert('Não foi possível marcar esse pagamento como pago.'),
    });
  }
}

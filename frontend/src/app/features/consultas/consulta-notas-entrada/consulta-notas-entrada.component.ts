import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotaEntradaService } from '../../../core/services/nota-entrada.service';
import { PagamentoService } from '../../../core/services/pagamento.service';
import { NotaEntrada } from '../../../core/models/nota-entrada.model';
import { Pagamento } from '../../../core/models/pagamento.model';

@Component({
  selector: 'app-consulta-notas-entrada',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './consulta-notas-entrada.component.html',
})
export class ConsultaNotasEntradaComponent implements OnInit {
  private notaEntradaService = inject(NotaEntradaService);
  private pagamentoService = inject(PagamentoService);

  notas = signal<NotaEntrada[]>([]);
  carregando = signal(true);

  ngOnInit(): void {
    this.carregarNotas();
  }

  carregarNotas(): void {
    this.carregando.set(true);
    this.notaEntradaService.listar().subscribe({
      next: (notas) => {
        // mais recentes primeiro
        this.notas.set([...notas].sort((a, b) => b.id - a.id));
        this.carregando.set(false);
      },
      error: () => this.carregando.set(false),
    });
  }

  cancelarNota(nota: NotaEntrada): void {
    const confirmou = confirm(
      `Cancelar a nota #${nota.numero}? O estoque dos itens será revertido e todas as parcelas de pagamento — inclusive as já pagas — serão canceladas.`
    );
    if (!confirmou) {
      return;
    }

    this.notaEntradaService.cancelar(nota.id).subscribe({
      next: () => this.carregarNotas(),
      error: () => alert('Não foi possível cancelar essa nota.'),
    });
  }

  marcarPagamentoComoPago(pagamento: Pagamento): void {
    this.pagamentoService.marcarComoPago(pagamento.id).subscribe({
      next: () => this.carregarNotas(),
      error: () => alert('Não foi possível marcar esse pagamento como pago.'),
    });
  }
}

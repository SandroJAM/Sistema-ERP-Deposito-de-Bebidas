import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NotaEntradaService } from '../../../core/services/nota-entrada.service';
import { PagamentoService } from '../../../core/services/pagamento.service';
import { FornecedorService } from '../../../core/services/fornecedor.service';
import { NotaEntrada } from '../../../core/models/nota-entrada.model';
import { Pagamento } from '../../../core/models/pagamento.model';
import { Fornecedor } from '../../../core/models/fornecedor.model';

type StatusFiltroNota = 'ATIVA' | 'CANCELADA' | null;

@Component({
  selector: 'app-consulta-notas-entrada',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './consulta-notas-entrada.component.html',
})
export class ConsultaNotasEntradaComponent implements OnInit {
  private notaEntradaService = inject(NotaEntradaService);
  private pagamentoService = inject(PagamentoService);
  private fornecedorService = inject(FornecedorService);

  fornecedores = signal<Fornecedor[]>([]);
  notas = signal<NotaEntrada[]>([]);
  carregando = signal(true);

  // ----- Filtros (aplicados no front, sobre a lista já carregada) -----
  numeroBusca = signal('');
  fornecedorSelecionadoId = signal<number | null>(null);
  statusSelecionado = signal<StatusFiltroNota>(null);
  dataDe = signal<string | null>(null);
  dataAte = signal<string | null>(null);

  notasFiltradas = computed(() => {
    const numero = this.numeroBusca().trim().toLowerCase();
    const fornecedorId = this.fornecedorSelecionadoId();
    const status = this.statusSelecionado();
    const de = this.dataDe();
    const ate = this.dataAte();

    return this.notas().filter((nota) => {
      if (numero && !nota.numero.toLowerCase().includes(numero)) {
        return false;
      }
      if (fornecedorId != null && nota.fornecedorId !== fornecedorId) {
        return false;
      }
      if (status && nota.status !== status) {
        return false;
      }
      if (de && nota.dataNota < de) {
        return false;
      }
      if (ate && nota.dataNota > ate) {
        return false;
      }
      return true;
    });
  });

  ngOnInit(): void {
    this.fornecedorService.listar().subscribe((fornecedores) => this.fornecedores.set(fornecedores));
    this.carregarNotas();
  }

  limparFiltros(): void {
    this.numeroBusca.set('');
    this.fornecedorSelecionadoId.set(null);
    this.statusSelecionado.set(null);
    this.dataDe.set(null);
    this.dataAte.set(null);
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

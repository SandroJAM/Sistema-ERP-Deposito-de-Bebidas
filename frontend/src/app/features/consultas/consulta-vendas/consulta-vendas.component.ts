import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { VendaService } from '../../../core/services/venda.service';
import { Venda } from '../../../core/models/venda.model';

@Component({
  selector: 'app-consulta-vendas',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './consulta-vendas.component.html',
})
export class ConsultaVendasComponent implements OnInit {
  private vendaService = inject(VendaService);

  vendas = signal<Venda[]>([]);
  carregando = signal(true);

  ngOnInit(): void {
    this.carregarVendas();
  }

  carregarVendas(): void {
    this.carregando.set(true);
    this.vendaService.listar().subscribe({
      next: (vendas) => {
        // mais recentes primeiro
        this.vendas.set([...vendas].sort((a, b) => b.id - a.id));
        this.carregando.set(false);
      },
      error: () => this.carregando.set(false),
    });
  }

  cancelarVenda(venda: Venda): void {
    const confirmou = confirm(`Cancelar a venda #${venda.id}? O estoque dos itens será devolvido.`);
    if (!confirmou) {
      return;
    }

    this.vendaService.cancelar(venda.id).subscribe({
      next: () => this.carregarVendas(),
      error: () => alert('Não foi possível cancelar essa venda.'),
    });
  }
}

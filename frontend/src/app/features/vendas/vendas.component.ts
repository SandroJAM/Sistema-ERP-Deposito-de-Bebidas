import { Component, OnDestroy, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, debounceTime, distinctUntilChanged, of, switchMap } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';
import { ProdutoService } from '../../core/services/produto.service';
import { ClienteService } from '../../core/services/cliente.service';
import { VendaService } from '../../core/services/venda.service';
import { Produto } from '../../core/models/produto.model';
import { Cliente } from '../../core/models/cliente.model';
import { Venda } from '../../core/models/venda.model';

interface ItemCarrinho {
  produto: Produto;
  quantidade: number;
}

@Component({
  selector: 'app-vendas',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './vendas.component.html',
})
export class VendasComponent implements OnInit, OnDestroy {
  private produtoService = inject(ProdutoService);
  private clienteService = inject(ClienteService);
  private vendaService = inject(VendaService);
  private authService = inject(AuthService);

  // ----- Montagem da venda -----
  clientes = signal<Cliente[]>([]);
  clienteSelecionadoId = signal<number | null>(null);

  termoBusca = signal('');
  sugestoes = signal<Produto[]>([]);
  buscandoSugestoes = signal(false);

  carrinho = signal<ItemCarrinho[]>([]);
  totalCarrinho = computed(() =>
    this.carrinho().reduce((soma, item) => soma + item.produto.preco * item.quantidade, 0)
  );

  finalizando = signal(false);
  erroFinalizar = signal<string | null>(null);
  sucesso = signal<string | null>(null);

  private buscaSubject = new Subject<string>();

  // ----- Vendas já registradas -----
  vendas = signal<Venda[]>([]);
  carregandoVendas = signal(true);

  ngOnInit(): void {
    this.clienteService.listar().subscribe((clientes) => this.clientes.set(clientes));
    this.carregarVendas();

    this.buscaSubject
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        switchMap((termo) => {
          if (!termo.trim()) {
            return of([]);
          }
          this.buscandoSugestoes.set(true);
          return this.produtoService.listar(termo);
        })
      )
      .subscribe((produtos) => {
        this.sugestoes.set(produtos);
        this.buscandoSugestoes.set(false);
      });
  }

  ngOnDestroy(): void {
    this.buscaSubject.complete();
  }

  carregarVendas(): void {
    this.carregandoVendas.set(true);
    this.vendaService.listar().subscribe({
      next: (vendas) => {
        // mais recentes primeiro
        this.vendas.set([...vendas].sort((a, b) => b.id - a.id));
        this.carregandoVendas.set(false);
      },
      error: () => this.carregandoVendas.set(false),
    });
  }

  aoDigitarBusca(valor: string): void {
    this.termoBusca.set(valor);
    this.buscaSubject.next(valor);
  }

  adicionarAoCarrinho(produto: Produto): void {
    const carrinhoAtual = this.carrinho();
    const existente = carrinhoAtual.find((item) => item.produto.id === produto.id);

    if (existente) {
      this.carrinho.set(
        carrinhoAtual.map((item) =>
          item.produto.id === produto.id ? { ...item, quantidade: item.quantidade + 1 } : item
        )
      );
    } else {
      this.carrinho.set([...carrinhoAtual, { produto, quantidade: 1 }]);
    }

    this.termoBusca.set('');
    this.sugestoes.set([]);
  }

  alterarQuantidade(produtoId: number, quantidade: number): void {
    if (quantidade < 1) {
      return;
    }
    this.carrinho.set(
      this.carrinho().map((item) => (item.produto.id === produtoId ? { ...item, quantidade } : item))
    );
  }

  removerDoCarrinho(produtoId: number): void {
    this.carrinho.set(this.carrinho().filter((item) => item.produto.id !== produtoId));
  }

  finalizarVenda(): void {
    if (this.carrinho().length === 0) {
      return;
    }

    this.finalizando.set(true);
    this.erroFinalizar.set(null);
    this.sucesso.set(null);

    const usuarioLogado = this.authService.usuarioLogado();

    this.vendaService
      .criar({
        usuarioId: usuarioLogado!.id,
        clienteId: this.clienteSelecionadoId(),
        itens: this.carrinho().map((item) => ({ produtoId: item.produto.id, quantidade: item.quantidade })),
      })
      .subscribe({
        next: (venda) => {
          this.finalizando.set(false);
          this.sucesso.set(`Venda #${venda.id} registrada — total de R$ ${venda.valorTotal.toFixed(2)}.`);
          this.carrinho.set([]);
          this.clienteSelecionadoId.set(null);
          this.carregarVendas();
        },
        error: (erro) => {
          this.finalizando.set(false);
          this.erroFinalizar.set(
            erro.status === 422
              ? erro.error?.message ?? 'Estoque insuficiente para um dos itens.'
              : 'Não foi possível registrar a venda.'
          );
        },
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

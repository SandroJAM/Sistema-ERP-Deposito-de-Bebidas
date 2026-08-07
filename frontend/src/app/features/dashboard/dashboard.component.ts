import { Component, OnDestroy, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, debounceTime, distinctUntilChanged, startWith, switchMap } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';
import { ProdutoService } from '../../core/services/produto.service';
import { Produto } from '../../core/models/produto.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard.component.html',
})
export class DashboardComponent implements OnInit, OnDestroy {
  private produtoService = inject(ProdutoService);
  authService = inject(AuthService);

  produtos = signal<Produto[]>([]);
  carregando = signal(true);
  erro = signal<string | null>(null);
  busca = signal('');

  produtosEstoqueBaixo = signal<Produto[]>([]);
  mostrarAlertaEstoque = signal(true);

  // Dispara a busca só 300ms depois da última tecla digitada, e ignora
  // termos repetidos — evita mandar uma requisição pra API a cada letra.
  private buscaSubject = new Subject<string>();

  ngOnInit(): void {
    this.carregarAlertaEstoque();

    this.buscaSubject
      .pipe(
        startWith(''),
        debounceTime(300),
        distinctUntilChanged(),
        switchMap((termo) => {
          this.carregando.set(true);
          this.erro.set(null);
          return this.produtoService.listar(termo);
        })
      )
      .subscribe({
        next: (produtos) => {
          this.produtos.set(produtos);
          this.carregando.set(false);
        },
        error: () => {
          this.erro.set('Não foi possível buscar os produtos.');
          this.carregando.set(false);
        },
      });
  }

  ngOnDestroy(): void {
    this.buscaSubject.complete();
  }

  aoDigitar(valor: string): void {
    this.busca.set(valor);
    this.buscaSubject.next(valor);
  }

  private carregarAlertaEstoque(): void {
    this.produtoService.listarEstoqueBaixo().subscribe({
      next: (produtos) => this.produtosEstoqueBaixo.set(produtos),
      error: () => this.produtosEstoqueBaixo.set([]),
    });
  }
}

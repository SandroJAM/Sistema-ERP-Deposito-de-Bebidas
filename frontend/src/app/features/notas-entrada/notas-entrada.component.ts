import { Component, OnDestroy, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, debounceTime, distinctUntilChanged, of, switchMap } from 'rxjs';
import { ProdutoService } from '../../core/services/produto.service';
import { FornecedorService } from '../../core/services/fornecedor.service';
import { NotaEntradaService } from '../../core/services/nota-entrada.service';
import { Produto } from '../../core/models/produto.model';
import { Fornecedor } from '../../core/models/fornecedor.model';
import { InputDinheiroDirective } from '../../shared/directives/input-dinheiro.directive';

interface ItemEntrada {
  produto: Produto;
  quantidade: number;
  valorUnitario: number;
}

function arredondar(valor: number): number {
  return Math.round((valor + Number.EPSILON) * 100) / 100;
}

function hoje(): string {
  return new Date().toISOString().slice(0, 10);
}

@Component({
  selector: 'app-notas-entrada',
  standalone: true,
  imports: [CommonModule, FormsModule, InputDinheiroDirective],
  templateUrl: './notas-entrada.component.html',
})
export class NotasEntradaComponent implements OnInit, OnDestroy {
  private produtoService = inject(ProdutoService);
  private fornecedorService = inject(FornecedorService);
  private notaEntradaService = inject(NotaEntradaService);

  fornecedores = signal<Fornecedor[]>([]);
  fornecedorSelecionadoId = signal<number | null>(null);

  numero = signal('');
  dataNota = signal(hoje());
  vencimento = signal(hoje());
  valorNota = signal<number | null>(null);

  termoBusca = signal('');
  sugestoes = signal<Produto[]>([]);
  buscandoSugestoes = signal(false);

  itens = signal<ItemEntrada[]>([]);
  somaItens = computed(() =>
    arredondar(this.itens().reduce((soma, item) => soma + item.quantidade * item.valorUnitario, 0))
  );
  diferenca = computed(() => arredondar((this.valorNota() ?? 0) - this.somaItens()));
  notaBate = computed(() => Math.abs(this.diferenca()) < 0.005);

  salvando = signal(false);
  erroSalvar = signal<string | null>(null);
  sucesso = signal<string | null>(null);

  private buscaSubject = new Subject<string>();

  ngOnInit(): void {
    this.fornecedorService.listar().subscribe((fornecedores) => this.fornecedores.set(fornecedores));

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

  aoDigitarBusca(valor: string): void {
    this.termoBusca.set(valor);
    this.buscaSubject.next(valor);
  }

  adicionarItem(produto: Produto): void {
    const itensAtuais = this.itens();
    const existente = itensAtuais.find((item) => item.produto.id === produto.id);

    if (existente) {
      this.itens.set(
        itensAtuais.map((item) =>
          item.produto.id === produto.id ? { ...item, quantidade: item.quantidade + 1 } : item
        )
      );
    } else {
      // preço de custo do produto entra como sugestão inicial — o valor real da nota pode ser editado.
      this.itens.set([...itensAtuais, { produto, quantidade: 1, valorUnitario: produto.precoCusto || 0 }]);
    }

    this.termoBusca.set('');
    this.sugestoes.set([]);
  }

  alterarQuantidadeItem(produtoId: number, quantidade: number): void {
    if (quantidade < 1) {
      return;
    }
    this.itens.set(this.itens().map((item) => (item.produto.id === produtoId ? { ...item, quantidade } : item)));
  }

  alterarValorUnitarioItem(produtoId: number, valorUnitario: number): void {
    if (valorUnitario < 0) {
      return;
    }
    this.itens.set(
      this.itens().map((item) => (item.produto.id === produtoId ? { ...item, valorUnitario } : item))
    );
  }

  removerItem(produtoId: number): void {
    this.itens.set(this.itens().filter((item) => item.produto.id !== produtoId));
  }

  /** Seleciona todo o conteúdo do campo ao focar, pra digitar já substituir o valor default. */
  selecionarTudo(event: FocusEvent): void {
    (event.target as HTMLInputElement).select();
  }

  usarSomaComoValorNota(): void {
    this.valorNota.set(this.somaItens());
  }

  registrarNota(): void {
    if (!this.numero().trim() || !this.dataNota() || !this.vencimento() || this.itens().length === 0) {
      return;
    }

    if (this.valorNota() == null || !this.notaBate()) {
      this.erroSalvar.set(
        `A soma dos itens (R$ ${this.somaItens().toFixed(2)}) não bate com o valor da nota. Ajuste um dos dois antes de continuar.`
      );
      return;
    }

    this.salvando.set(true);
    this.erroSalvar.set(null);
    this.sucesso.set(null);

    this.notaEntradaService
      .criar({
        numero: this.numero().trim(),
        fornecedorId: this.fornecedorSelecionadoId(),
        dataNota: this.dataNota(),
        valorNota: this.valorNota()!,
        vencimento: this.vencimento(),
        itens: this.itens().map((item) => ({
          produtoId: item.produto.id,
          quantidade: item.quantidade,
          valorUnitario: item.valorUnitario,
        })),
      })
      .subscribe({
        next: (nota) => {
          this.salvando.set(false);
          this.sucesso.set(
            `Nota #${nota.numero} registrada — estoque atualizado e pagamento gerado com vencimento em ${this.formatarData(nota.vencimento)}. Confira em Consultas > Notas de Entrada.`
          );
          this.limparFormulario();
        },
        error: (erro) => {
          this.salvando.set(false);
          this.erroSalvar.set(
            erro.status === 422
              ? erro.error?.message ?? 'A soma dos itens não bate com o valor da nota.'
              : 'Não foi possível registrar a nota de entrada.'
          );
        },
      });
  }

  private limparFormulario(): void {
    this.numero.set('');
    this.fornecedorSelecionadoId.set(null);
    this.dataNota.set(hoje());
    this.vencimento.set(hoje());
    this.valorNota.set(null);
    this.itens.set([]);
  }

  private formatarData(iso: string): string {
    const [ano, mes, dia] = iso.split('-');
    return `${dia}/${mes}/${ano}`;
  }
}

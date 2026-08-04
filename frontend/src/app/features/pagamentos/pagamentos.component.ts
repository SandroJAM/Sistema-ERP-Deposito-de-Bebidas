import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { PagamentoService } from '../../core/services/pagamento.service';
import { FornecedorService } from '../../core/services/fornecedor.service';
import { AuthService } from '../../core/services/auth.service';
import { Pagamento } from '../../core/models/pagamento.model';
import { Fornecedor } from '../../core/models/fornecedor.model';
import { ModalComponent } from '../../shared/ui/modal/modal.component';

type StatusFiltro = 'PENDENTE' | 'PAGO' | 'CANCELADO' | null;

function hoje(): string {
  return new Date().toISOString().slice(0, 10);
}

@Component({
  selector: 'app-pagamentos',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, ModalComponent],
  templateUrl: './pagamentos.component.html',
})
export class PagamentosComponent implements OnInit {
  authService = inject(AuthService);
  private pagamentoService = inject(PagamentoService);
  private fornecedorService = inject(FornecedorService);
  private fb = inject(FormBuilder);

  fornecedores = signal<Fornecedor[]>([]);
  pagamentos = signal<Pagamento[]>([]);
  carregando = signal(true);
  erro = signal<string | null>(null);

  // ----- Filtros -----
  fornecedorSelecionadoId = signal<number | null>(null);
  statusSelecionado = signal<StatusFiltro>(null);
  vencimentoDe = signal<string | null>(null);
  vencimentoAte = signal<string | null>(null);

  // ----- Modal de criação/edição -----
  modalAberto = signal(false);
  pagamentoEmEdicao = signal<Pagamento | null>(null);
  salvando = signal(false);
  erroSalvar = signal<string | null>(null);

  /** Só pagamentos avulsos permitem trocar o fornecedor; os gerados por nota mostram o dela, fixo. */
  get ehAvulsoOuNovo(): boolean {
    const emEdicao = this.pagamentoEmEdicao();
    return !emEdicao || emEdicao.origem === 'AVULSO';
  }

  form = this.fb.group({
    fornecedorId: [null as number | null, Validators.required],
    numeroFatura: ['', Validators.required],
    numeroParcela: [1, [Validators.required, Validators.min(1)]],
    dataEmissao: [hoje(), Validators.required],
    dataVencimento: [hoje(), Validators.required],
    valorAPagar: [0, [Validators.required, Validators.min(0.01)]],
    descricao: [''],
    status: ['PENDENTE' as 'PENDENTE' | 'PAGO' | 'CANCELADO', Validators.required],
  });

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
    this.erro.set(null);
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
        error: () => {
          this.erro.set('Não foi possível carregar os pagamentos.');
          this.carregando.set(false);
        },
      });
  }

  abrirNovo(): void {
    this.pagamentoEmEdicao.set(null);
    this.erroSalvar.set(null);
    this.form.reset({
      fornecedorId: null,
      numeroFatura: '',
      numeroParcela: 1,
      dataEmissao: hoje(),
      dataVencimento: hoje(),
      valorAPagar: 0,
      descricao: '',
      status: 'PENDENTE',
    });
    this.form.get('fornecedorId')?.enable();
    this.modalAberto.set(true);
  }

  abrirEdicao(pagamento: Pagamento): void {
    this.pagamentoEmEdicao.set(pagamento);
    this.erroSalvar.set(null);
    this.form.reset({
      fornecedorId: pagamento.fornecedorId,
      numeroFatura: pagamento.numeroFatura,
      numeroParcela: pagamento.numeroParcela,
      dataEmissao: pagamento.dataEmissao,
      dataVencimento: pagamento.dataVencimento,
      valorAPagar: pagamento.valorAPagar,
      descricao: pagamento.descricao ?? '',
      status: pagamento.status,
    });
    // Pagamento vindo de nota de entrada: fornecedor é o da nota, não dá pra editar por aqui.
    if (pagamento.origem === 'NOTA_ENTRADA') {
      this.form.get('fornecedorId')?.disable();
    } else {
      this.form.get('fornecedorId')?.enable();
    }
    this.modalAberto.set(true);
  }

  fecharModal(): void {
    this.modalAberto.set(false);
  }

  salvar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.salvando.set(true);
    this.erroSalvar.set(null);
    const valores = this.form.getRawValue();
    const emEdicao = this.pagamentoEmEdicao();

    const dados = {
      fornecedorId: valores.fornecedorId,
      numeroFatura: valores.numeroFatura!.trim(),
      numeroParcela: valores.numeroParcela!,
      dataEmissao: valores.dataEmissao!,
      dataVencimento: valores.dataVencimento!,
      valorAPagar: valores.valorAPagar!,
      descricao: valores.descricao?.trim() || null,
      status: valores.status!,
    };

    const operacao = emEdicao
      ? this.pagamentoService.atualizar(emEdicao.id, dados)
      : this.pagamentoService.criar(dados);

    operacao.subscribe({
      next: () => {
        this.salvando.set(false);
        this.modalAberto.set(false);
        this.carregarPagamentos();
      },
      error: (erro) => {
        this.salvando.set(false);
        this.erroSalvar.set(erro.error?.message ?? 'Não foi possível salvar o pagamento.');
      },
    });
  }

  marcarComoPago(pagamento: Pagamento): void {
    this.pagamentoService.marcarComoPago(pagamento.id).subscribe({
      next: () => this.carregarPagamentos(),
      error: () => alert('Não foi possível marcar esse pagamento como pago.'),
    });
  }

  excluir(pagamento: Pagamento): void {
    const confirmou = confirm(
      `Excluir o pagamento "${pagamento.numeroFatura} (parcela ${pagamento.numeroParcela})"? Essa ação não pode ser desfeita.`
    );
    if (!confirmou) {
      return;
    }

    this.pagamentoService.excluir(pagamento.id).subscribe({
      next: () => this.carregarPagamentos(),
      error: () => alert('Não foi possível excluir esse pagamento.'),
    });
  }
}

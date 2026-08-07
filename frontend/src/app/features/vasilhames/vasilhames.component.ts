import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';
import { ClienteService } from '../../core/services/cliente.service';
import { TipoCascoService } from '../../core/services/tipo-casco.service';
import { MovimentoCascoService } from '../../core/services/movimento-casco.service';
import { Cliente } from '../../core/models/cliente.model';
import { MovimentoCasco, SaldoCasco, TipoCasco, TipoMovimentoCasco } from '../../core/models/casco.model';
import { ModalComponent } from '../../shared/ui/modal/modal.component';

@Component({
  selector: 'app-vasilhames',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, ModalComponent],
  templateUrl: './vasilhames.component.html',
})
export class VasilhamesComponent implements OnInit {
  authService = inject(AuthService);
  private clienteService = inject(ClienteService);
  private tipoCascoService = inject(TipoCascoService);
  private movimentoCascoService = inject(MovimentoCascoService);
  private fb = inject(FormBuilder);

  clientes = signal<Cliente[]>([]);
  tiposCasco = signal<TipoCasco[]>([]);
  saldos = signal<SaldoCasco[]>([]);
  movimentos = signal<MovimentoCasco[]>([]);
  carregando = signal(true);
  erro = signal<string | null>(null);

  abaAtiva = signal<'saldos' | 'historico'>('saldos');

  // ----- Form de registro de movimento -----
  modalMovimentoAberto = signal(false);
  registrandoMovimento = signal(false);

  formMovimento = this.fb.group({
    clienteId: [null as number | null, [Validators.required]],
    tipoCascoId: [null as number | null, [Validators.required]],
    tipoMovimento: ['SAIDA' as TipoMovimentoCasco, [Validators.required]],
    quantidade: [1, [Validators.required, Validators.min(1)]],
    observacao: [''],
  });

  // ----- Modal de cadastro de tipos de casco -----
  modalTipoAberto = signal(false);
  tipoEmEdicao = signal<TipoCasco | null>(null);
  salvandoTipo = signal(false);

  formTipo = this.fb.group({
    nome: ['', [Validators.required, Validators.minLength(2)]],
    valorReposicao: [0, [Validators.required, Validators.min(0)]],
    ativo: [true],
  });

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.carregando.set(true);
    this.erro.set(null);

    forkJoin({
      clientes: this.clienteService.listar(),
      tiposCasco: this.tipoCascoService.listar(),
      saldos: this.movimentoCascoService.listarSaldos(),
      movimentos: this.movimentoCascoService.listar(),
    }).subscribe({
      next: ({ clientes, tiposCasco, saldos, movimentos }) => {
        this.clientes.set(clientes);
        this.tiposCasco.set(tiposCasco);
        this.saldos.set(saldos);
        this.movimentos.set(movimentos);
        this.carregando.set(false);
      },
      error: () => {
        this.erro.set('Não foi possível carregar os dados de vasilhame.');
        this.carregando.set(false);
      },
    });
  }

  tiposCascoAtivos(): TipoCasco[] {
    return this.tiposCasco().filter((t) => t.ativo);
  }

  // ----- Registro de movimento -----

  abrirRegistroMovimento(saldo?: SaldoCasco): void {
    if (this.tiposCascoAtivos().length === 0) {
      alert('Cadastre pelo menos um tipo de casco antes de registrar um movimento.');
      return;
    }
    this.formMovimento.reset({
      clienteId: saldo?.clienteId ?? null,
      tipoCascoId: saldo?.tipoCascoId ?? this.tiposCascoAtivos()[0].id,
      tipoMovimento: saldo ? 'DEVOLUCAO' : 'SAIDA',
      quantidade: 1,
      observacao: '',
    });
    this.modalMovimentoAberto.set(true);
  }

  fecharModalMovimento(): void {
    this.modalMovimentoAberto.set(false);
  }

  registrarMovimento(): void {
    if (this.formMovimento.invalid) {
      this.formMovimento.markAllAsTouched();
      return;
    }

    this.registrandoMovimento.set(true);
    const valores = this.formMovimento.getRawValue();

    this.movimentoCascoService
      .registrar({
        clienteId: Number(valores.clienteId),
        tipoCascoId: Number(valores.tipoCascoId),
        tipoMovimento: valores.tipoMovimento!,
        quantidade: Number(valores.quantidade),
        observacao: valores.observacao || null,
      })
      .subscribe({
        next: () => {
          this.registrandoMovimento.set(false);
          this.modalMovimentoAberto.set(false);
          this.carregar();
        },
        error: (err) => {
          this.registrandoMovimento.set(false);
          alert(err?.error?.message ?? 'Não foi possível registrar o movimento.');
        },
      });
  }

  excluirMovimento(movimento: MovimentoCasco): void {
    const confirmou = confirm(`Remover este lançamento de ${movimento.quantidade}x "${movimento.tipoCascoNome}"?`);
    if (!confirmou) {
      return;
    }

    this.movimentoCascoService.excluir(movimento.id).subscribe({
      next: () => this.carregar(),
      error: () => alert('Não foi possível remover esse lançamento.'),
    });
  }

  // ----- Cadastro de tipos de casco -----

  abrirNovoTipo(): void {
    this.tipoEmEdicao.set(null);
    this.formTipo.reset({ nome: '', valorReposicao: 0, ativo: true });
    this.modalTipoAberto.set(true);
  }

  abrirEdicaoTipo(tipo: TipoCasco): void {
    this.tipoEmEdicao.set(tipo);
    this.formTipo.reset({ nome: tipo.nome, valorReposicao: tipo.valorReposicao, ativo: tipo.ativo });
    this.modalTipoAberto.set(true);
  }

  fecharModalTipo(): void {
    this.modalTipoAberto.set(false);
  }

  salvarTipo(): void {
    if (this.formTipo.invalid) {
      this.formTipo.markAllAsTouched();
      return;
    }

    this.salvandoTipo.set(true);
    const valores = this.formTipo.getRawValue();
    const dados = {
      nome: valores.nome!,
      valorReposicao: Number(valores.valorReposicao),
      ativo: !!valores.ativo,
    };

    const emEdicao = this.tipoEmEdicao();
    const operacao = emEdicao
      ? this.tipoCascoService.atualizar(emEdicao.id, dados)
      : this.tipoCascoService.criar(dados);

    operacao.subscribe({
      next: () => {
        this.salvandoTipo.set(false);
        this.modalTipoAberto.set(false);
        this.carregar();
      },
      error: () => {
        this.salvandoTipo.set(false);
      },
    });
  }

  excluirTipo(tipo: TipoCasco): void {
    const confirmou = confirm(`Excluir o tipo de casco "${tipo.nome}"? Essa ação não pode ser desfeita.`);
    if (!confirmou) {
      return;
    }

    this.tipoCascoService.excluir(tipo.id).subscribe({
      next: () => this.carregar(),
      error: () => alert('Não foi possível excluir. Verifique se ainda existem movimentos usando esse tipo.'),
    });
  }
}

import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { FornecedorService } from '../../core/services/fornecedor.service';
import { AuthService } from '../../core/services/auth.service';
import { Fornecedor } from '../../core/models/fornecedor.model';
import { ModalComponent } from '../../shared/ui/modal/modal.component';

@Component({
  selector: 'app-fornecedores',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ModalComponent],
  templateUrl: './fornecedores.component.html',
})
export class FornecedoresComponent implements OnInit {
  authService = inject(AuthService);
  private fornecedorService = inject(FornecedorService);
  private fb = inject(FormBuilder);

  fornecedores = signal<Fornecedor[]>([]);
  carregando = signal(true);
  erro = signal<string | null>(null);

  modalAberto = signal(false);
  fornecedorEmEdicao = signal<Fornecedor | null>(null);
  salvando = signal(false);

  form = this.fb.group({
    nome: ['', [Validators.required, Validators.minLength(2)]],
    cnpjCpf: [''],
    telefone: [''],
    email: ['', [Validators.email]],
    ativo: [true],
  });

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.carregando.set(true);
    this.erro.set(null);

    this.fornecedorService.listar().subscribe({
      next: (fornecedores) => {
        this.fornecedores.set(fornecedores);
        this.carregando.set(false);
      },
      error: () => {
        this.erro.set('Não foi possível carregar os fornecedores.');
        this.carregando.set(false);
      },
    });
  }

  abrirNovo(): void {
    this.fornecedorEmEdicao.set(null);
    this.form.reset({ nome: '', cnpjCpf: '', telefone: '', email: '', ativo: true });
    this.modalAberto.set(true);
  }

  abrirEdicao(fornecedor: Fornecedor): void {
    this.fornecedorEmEdicao.set(fornecedor);
    this.form.reset({
      nome: fornecedor.nome,
      cnpjCpf: fornecedor.cnpjCpf ?? '',
      telefone: fornecedor.telefone ?? '',
      email: fornecedor.email ?? '',
      ativo: fornecedor.ativo,
    });
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
    const valores = this.form.getRawValue();
    const dados = {
      nome: valores.nome!,
      cnpjCpf: valores.cnpjCpf || null,
      telefone: valores.telefone || null,
      email: valores.email || null,
      ativo: !!valores.ativo,
    };

    const emEdicao = this.fornecedorEmEdicao();
    const operacao = emEdicao
      ? this.fornecedorService.atualizar(emEdicao.id, dados)
      : this.fornecedorService.criar(dados);

    operacao.subscribe({
      next: () => {
        this.salvando.set(false);
        this.modalAberto.set(false);
        this.carregar();
      },
      error: () => {
        this.salvando.set(false);
      },
    });
  }

  excluir(fornecedor: Fornecedor): void {
    const confirmou = confirm(`Excluir o fornecedor "${fornecedor.nome}"? Essa ação não pode ser desfeita.`);
    if (!confirmou) {
      return;
    }

    this.fornecedorService.excluir(fornecedor.id).subscribe({
      next: () => this.carregar(),
      error: () => alert('Não foi possível excluir. Verifique se ainda existem produtos usando esse fornecedor.'),
    });
  }
}

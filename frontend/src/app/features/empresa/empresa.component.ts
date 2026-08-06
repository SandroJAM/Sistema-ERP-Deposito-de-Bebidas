import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { EmpresaService } from '../../core/services/empresa.service';
import { AuthService } from '../../core/services/auth.service';
import { Empresa } from '../../core/models/empresa.model';
import { ModalComponent } from '../../shared/ui/modal/modal.component';

@Component({
  selector: 'app-empresa',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ModalComponent],
  templateUrl: './empresa.component.html',
})
export class EmpresaComponent implements OnInit {
  authService = inject(AuthService);
  private empresaService = inject(EmpresaService);
  private fb = inject(FormBuilder);

  empresas = signal<Empresa[]>([]);
  carregando = signal(true);
  erro = signal<string | null>(null);

  modalAberto = signal(false);
  empresaEmEdicao = signal<Empresa | null>(null);
  salvando = signal(false);

  form = this.fb.group({
    nome: ['', [Validators.required, Validators.minLength(2)]],
    endereco: [''],
    telefone: [''],
  });

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.carregando.set(true);
    this.empresaService.listar().subscribe({
      next: (empresas) => {
        this.empresas.set(empresas);
        this.carregando.set(false);
      },
      error: () => {
        this.erro.set('Não foi possível carregar o cadastro da empresa.');
        this.carregando.set(false);
      },
    });
  }

  abrirNovo(): void {
    this.empresaEmEdicao.set(null);
    this.form.reset({ nome: '', endereco: '', telefone: '' });
    this.modalAberto.set(true);
  }

  abrirEdicao(empresa: Empresa): void {
    this.empresaEmEdicao.set(empresa);
    this.form.reset({ nome: empresa.nome, endereco: empresa.endereco ?? '', telefone: empresa.telefone ?? '' });
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
      endereco: valores.endereco || null,
      telefone: valores.telefone || null,
    };
    const emEdicao = this.empresaEmEdicao();

    const operacao = emEdicao
      ? this.empresaService.atualizar(emEdicao.id, dados)
      : this.empresaService.criar(dados);

    operacao.subscribe({
      next: () => {
        this.salvando.set(false);
        this.modalAberto.set(false);
        this.carregar();
      },
      error: (err) => {
        this.salvando.set(false);
        alert(err?.error?.message ?? 'Não foi possível salvar o cadastro da empresa.');
      },
    });
  }

  excluir(empresa: Empresa): void {
    const confirmou = confirm(
      `Excluir o cadastro de "${empresa.nome}"? O nome/endereço/telefone somem do topo das telas e do histórico.`
    );
    if (!confirmou) {
      return;
    }

    this.empresaService.excluir(empresa.id).subscribe({
      next: () => this.carregar(),
      error: () => alert('Não foi possível excluir esse cadastro.'),
    });
  }
}

import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CategoriaService } from '../../core/services/categoria.service';
import { AuthService } from '../../core/services/auth.service';
import { Categoria } from '../../core/models/categoria.model';
import { ModalComponent } from '../../shared/ui/modal/modal.component';

@Component({
  selector: 'app-categorias',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ModalComponent],
  templateUrl: './categorias.component.html',
})
export class CategoriasComponent implements OnInit {
  authService = inject(AuthService);
  private categoriaService = inject(CategoriaService);
  private fb = inject(FormBuilder);

  categorias = signal<Categoria[]>([]);
  carregando = signal(true);
  erro = signal<string | null>(null);

  modalAberto = signal(false);
  categoriaEmEdicao = signal<Categoria | null>(null);
  salvando = signal(false);

  form = this.fb.group({
    nome: ['', [Validators.required, Validators.minLength(2)]],
  });

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.carregando.set(true);
    this.categoriaService.listar().subscribe({
      next: (categorias) => {
        this.categorias.set(categorias);
        this.carregando.set(false);
      },
      error: () => {
        this.erro.set('Não foi possível carregar as categorias.');
        this.carregando.set(false);
      },
    });
  }

  abrirNova(): void {
    this.categoriaEmEdicao.set(null);
    this.form.reset({ nome: '' });
    this.modalAberto.set(true);
  }

  abrirEdicao(categoria: Categoria): void {
    this.categoriaEmEdicao.set(categoria);
    this.form.reset({ nome: categoria.nome });
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
    const dados = { nome: this.form.getRawValue().nome! };
    const emEdicao = this.categoriaEmEdicao();

    const operacao = emEdicao
      ? this.categoriaService.atualizar(emEdicao.id, dados)
      : this.categoriaService.criar(dados);

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

  excluir(categoria: Categoria): void {
    const confirmou = confirm(`Excluir a categoria "${categoria.nome}"? Essa ação não pode ser desfeita.`);
    if (!confirmou) {
      return;
    }

    this.categoriaService.excluir(categoria.id).subscribe({
      next: () => this.carregar(),
      error: () => alert('Não foi possível excluir. Verifique se ainda existem produtos usando essa categoria.'),
    });
  }
}

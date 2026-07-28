import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';
import { ProdutoService } from '../../core/services/produto.service';
import { CategoriaService } from '../../core/services/categoria.service';
import { FornecedorService } from '../../core/services/fornecedor.service';
import { Produto } from '../../core/models/produto.model';
import { Categoria } from '../../core/models/categoria.model';
import { Fornecedor } from '../../core/models/fornecedor.model';
import { ModalComponent } from '../../shared/ui/modal/modal.component';

@Component({
  selector: 'app-produtos',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ModalComponent],
  templateUrl: './produtos.component.html',
})
export class ProdutosComponent implements OnInit {
  authService = inject(AuthService);
  private produtoService = inject(ProdutoService);
  private categoriaService = inject(CategoriaService);
  private fornecedorService = inject(FornecedorService);
  private fb = inject(FormBuilder);

  produtos = signal<Produto[]>([]);
  categorias = signal<Categoria[]>([]);
  fornecedores = signal<Fornecedor[]>([]);
  carregando = signal(true);
  erro = signal<string | null>(null);

  modalAberto = signal(false);
  produtoEmEdicao = signal<Produto | null>(null);
  salvando = signal(false);

  form = this.fb.group({
    nome: ['', [Validators.required, Validators.minLength(2)]],
    unidade: [''],
    preco: [0, [Validators.required, Validators.min(0)]],
    precoCusto: [0, [Validators.required, Validators.min(0)]],
    estoqueAtual: [0, [Validators.required, Validators.min(0)]],
    ativo: [true],
    categoriaId: [null as number | null, [Validators.required]],
    fornecedorId: [null as number | null],
  });

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.carregando.set(true);
    this.erro.set(null);

    forkJoin({
      produtos: this.produtoService.listar(),
      categorias: this.categoriaService.listar(),
      fornecedores: this.fornecedorService.listar(),
    }).subscribe({
      next: ({ produtos, categorias, fornecedores }) => {
        this.produtos.set(produtos);
        this.categorias.set(categorias);
        this.fornecedores.set(fornecedores);
        this.carregando.set(false);
      },
      error: () => {
        this.erro.set('Não foi possível carregar os produtos.');
        this.carregando.set(false);
      },
    });
  }

  abrirNovo(): void {
    if (this.categorias().length === 0) {
      alert('Cadastre pelo menos uma categoria antes de criar um produto.');
      return;
    }
    this.produtoEmEdicao.set(null);
    this.form.reset({
      nome: '',
      unidade: '',
      preco: 0,
      precoCusto: 0,
      estoqueAtual: 0,
      ativo: true,
      categoriaId: this.categorias()[0].id,
      fornecedorId: null,
    });
    this.modalAberto.set(true);
  }

  abrirEdicao(produto: Produto): void {
    this.produtoEmEdicao.set(produto);
    this.form.reset({
      nome: produto.nome,
      unidade: produto.unidade ?? '',
      preco: produto.preco,
      precoCusto: produto.precoCusto,
      estoqueAtual: produto.estoqueAtual,
      ativo: produto.ativo,
      categoriaId: produto.categoriaId,
      fornecedorId: produto.fornecedorId,
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
      unidade: valores.unidade || null,
      preco: Number(valores.preco),
      precoCusto: Number(valores.precoCusto),
      estoqueAtual: Number(valores.estoqueAtual),
      ativo: !!valores.ativo,
      categoriaId: Number(valores.categoriaId),
      fornecedorId: valores.fornecedorId ? Number(valores.fornecedorId) : null,
    };

    const emEdicao = this.produtoEmEdicao();
    const operacao = emEdicao
      ? this.produtoService.atualizar(emEdicao.id, dados)
      : this.produtoService.criar(dados);

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

  excluir(produto: Produto): void {
    const confirmou = confirm(`Excluir o produto "${produto.nome}"? Essa ação não pode ser desfeita.`);
    if (!confirmou) {
      return;
    }

    this.produtoService.excluir(produto.id).subscribe({
      next: () => this.carregar(),
      error: () => alert('Não foi possível excluir esse produto.'),
    });
  }
}

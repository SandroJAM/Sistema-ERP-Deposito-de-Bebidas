import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { UsuarioService } from '../../core/services/usuario.service';
import { Usuario } from '../../core/models/usuario.model';
import { ModalComponent } from '../../shared/ui/modal/modal.component';

@Component({
  selector: 'app-usuarios',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ModalComponent],
  templateUrl: './usuarios.component.html',
})
export class UsuariosComponent implements OnInit {
  private usuarioService = inject(UsuarioService);
  private fb = inject(FormBuilder);
  authService = inject(AuthService);

  usuarios = signal<Usuario[]>([]);
  carregando = signal(true);
  erro = signal<string | null>(null);

  modalAberto = signal(false);
  usuarioEmEdicao = signal<Usuario | null>(null);
  salvando = signal(false);
  erroSalvar = signal<string | null>(null);

  form = this.fb.group({
    nome: ['', [Validators.required, Validators.minLength(2)]],
    email: ['', [Validators.required, Validators.email]],
    senha: [''],
    perfil: ['VENDEDOR' as 'ADMIN' | 'VENDEDOR', Validators.required],
  });

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.carregando.set(true);
    this.usuarioService.listar().subscribe({
      next: (usuarios) => {
        this.usuarios.set(usuarios);
        this.carregando.set(false);
      },
      error: () => {
        this.erro.set('Não foi possível carregar os usuários.');
        this.carregando.set(false);
      },
    });
  }

  abrirNovo(): void {
    this.usuarioEmEdicao.set(null);
    this.erroSalvar.set(null);
    this.form.reset({ nome: '', email: '', senha: '', perfil: 'VENDEDOR' });
    this.modalAberto.set(true);
  }

  abrirEdicao(usuario: Usuario): void {
    this.usuarioEmEdicao.set(usuario);
    this.erroSalvar.set(null);
    this.form.reset({ nome: usuario.nome, email: usuario.email, senha: '', perfil: usuario.perfil });
    this.modalAberto.set(true);
  }

  fecharModal(): void {
    this.modalAberto.set(false);
  }

  salvar(): void {
    const emEdicao = this.usuarioEmEdicao();

    if (this.form.invalid || (!emEdicao && !this.form.value.senha?.trim())) {
      this.form.markAllAsTouched();
      if (!emEdicao && !this.form.value.senha?.trim()) {
        this.erroSalvar.set('A senha é obrigatória para um novo usuário.');
      }
      return;
    }

    this.salvando.set(true);
    this.erroSalvar.set(null);
    const valores = this.form.getRawValue();
    const dados = {
      nome: valores.nome!,
      email: valores.email!,
      perfil: valores.perfil!,
      // em branco na edição = mantém a senha atual (o back-end ignora se vier vazio)
      senha: valores.senha?.trim() || undefined,
    };

    const operacao = emEdicao
      ? this.usuarioService.atualizar(emEdicao.id, dados)
      : this.usuarioService.criar(dados);

    operacao.subscribe({
      next: () => {
        this.salvando.set(false);
        this.modalAberto.set(false);
        this.carregar();
      },
      error: (erro) => {
        this.salvando.set(false);
        this.erroSalvar.set(erro.error?.message ?? 'Não foi possível salvar esse usuário.');
      },
    });
  }

  excluir(usuario: Usuario): void {
    const confirmou = confirm(`Excluir o usuário "${usuario.nome}"? Essa ação não pode ser desfeita.`);
    if (!confirmou) {
      return;
    }

    this.usuarioService.excluir(usuario.id).subscribe({
      next: () => this.carregar(),
      error: (erro) => alert(erro.error?.message ?? 'Não foi possível excluir esse usuário.'),
    });
  }
}

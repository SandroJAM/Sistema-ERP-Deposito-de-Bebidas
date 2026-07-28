import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ClienteService } from '../../core/services/cliente.service';
import { AuthService } from '../../core/services/auth.service';
import { Cliente } from '../../core/models/cliente.model';
import { ModalComponent } from '../../shared/ui/modal/modal.component';

@Component({
  selector: 'app-clientes',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ModalComponent],
  templateUrl: './clientes.component.html',
})
export class ClientesComponent implements OnInit {
  authService = inject(AuthService);
  private clienteService = inject(ClienteService);
  private fb = inject(FormBuilder);

  clientes = signal<Cliente[]>([]);
  carregando = signal(true);
  erro = signal<string | null>(null);

  modalAberto = signal(false);
  clienteEmEdicao = signal<Cliente | null>(null);
  salvando = signal(false);

  form = this.fb.group({
    nome: ['', [Validators.required, Validators.minLength(2)]],
    telefone: [''],
  });

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.carregando.set(true);
    this.clienteService.listar().subscribe({
      next: (clientes) => {
        this.clientes.set(clientes);
        this.carregando.set(false);
      },
      error: () => {
        this.erro.set('Não foi possível carregar os clientes.');
        this.carregando.set(false);
      },
    });
  }

  abrirNovo(): void {
    this.clienteEmEdicao.set(null);
    this.form.reset({ nome: '', telefone: '' });
    this.modalAberto.set(true);
  }

  abrirEdicao(cliente: Cliente): void {
    this.clienteEmEdicao.set(cliente);
    this.form.reset({ nome: cliente.nome, telefone: cliente.telefone ?? '' });
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
    const dados = { nome: valores.nome!, telefone: valores.telefone || null };
    const emEdicao = this.clienteEmEdicao();

    const operacao = emEdicao
      ? this.clienteService.atualizar(emEdicao.id, dados)
      : this.clienteService.criar(dados);

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

  excluir(cliente: Cliente): void {
    const confirmou = confirm(`Excluir o cliente "${cliente.nome}"? Essa ação não pode ser desfeita.`);
    if (!confirmou) {
      return;
    }

    this.clienteService.excluir(cliente.id).subscribe({
      next: () => this.carregar(),
      error: () => alert('Não foi possível excluir esse cliente.'),
    });
  }
}

import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { EmpresaService } from '../../core/services/empresa.service';
import { Empresa } from '../../core/models/empresa.model';

interface ItemMenu {
  rota: string;
  rotulo: string;
  icone: string;
  somenteAdmin?: boolean;
}

interface GrupoMenu {
  titulo: string;
  itens: ItemMenu[];
}

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './shell.component.html',
  styleUrl: './shell.component.scss',
})
export class ShellComponent implements OnInit {
  private empresaService = inject(EmpresaService);

  empresa = signal<Empresa | null>(null);

  itemInicio: ItemMenu = {
    rota: '/',
    rotulo: 'Início',
    icone: 'M4 11 12 4l8 7v8a1 1 0 0 1-1 1h-4v-6H9v6H5a1 1 0 0 1-1-1z',
  };

  gruposMenu: GrupoMenu[] = [
    {
      titulo: 'Cadastros',
      itens: [
        { rota: '/produtos', rotulo: 'Produtos', icone: 'M4 7h16l-1 13H5zM8 7V5a4 4 0 0 1 8 0v2' },
        { rota: '/categorias', rotulo: 'Categorias', icone: 'M4 4h7v7H4zM13 4h7v7h-7zM4 13h7v7H4zM13 13h7v7h-7z' },
        { rota: '/fornecedores', rotulo: 'Fornecedores', icone: 'M3 21h18M5 21V8l7-4 7 4v13M9 21v-6h6v6' },
        { rota: '/clientes', rotulo: 'Clientes', icone: 'M12 12a4 4 0 1 0 0-8 4 4 0 0 0 0 8ZM4 20a8 8 0 0 1 16 0' },
        {
          rota: '/usuarios',
          rotulo: 'Usuários',
          icone: 'M16 7a4 4 0 1 1-8 0 4 4 0 0 1 8 0ZM3 21a7 7 0 0 1 14 0M17 11a3 3 0 1 0 0-6M21 21a6 6 0 0 0-4-5.65',
          somenteAdmin: true,
        },
        {
          rota: '/empresa',
          rotulo: 'Empresa',
          icone: 'M4 21V4a1 1 0 0 1 1-1h9a1 1 0 0 1 1 1v17M9 8h.01M9 12h.01M9 16h.01M15 21v-4h4v4M15 3h5v18',
          somenteAdmin: true,
        },
      ],
    },
    {
      titulo: 'Movimentação',
      itens: [
        { rota: '/notas-entrada', rotulo: 'Notas de Entrada', icone: 'M9 3h6l3 3v15H6V6zM9 3v4h6M9 12h6M9 16h4' },
        { rota: '/vendas', rotulo: 'Vendas', icone: 'M3 3h2l2.4 12.4a2 2 0 0 0 2 1.6h7.2a2 2 0 0 0 2-1.6L21 8H6' },
        { rota: '/pagamentos', rotulo: 'Pagamentos', icone: 'M3 6h18v12H3zM3 10h18M7 15h4' },
      ],
    },
    {
      titulo: 'Consultas',
      itens: [
        {
          rota: '/consultas/notas-entrada',
          rotulo: 'Notas de Entrada',
          icone: 'M9 3h6l3 3v15H6V6zM9 3v4h6M9 12h6M9 16h4',
        },
        {
          rota: '/consultas/vendas',
          rotulo: 'Vendas',
          icone: 'M3 3h2l2.4 12.4a2 2 0 0 0 2 1.6h7.2a2 2 0 0 0 2-1.6L21 8H6',
        },
        {
          rota: '/consultas/pagamentos',
          rotulo: 'Pagamentos Fornecedor',
          icone: 'M3 6h18v12H3zM3 10h18M7 15h4',
        },
      ],
    },
  ];

  constructor(public authService: AuthService) {}

  ngOnInit(): void {
    this.empresaService.buscarAtual().subscribe((empresa) => this.empresa.set(empresa));
  }

  sair(): void {
    this.authService.logout();
  }
}

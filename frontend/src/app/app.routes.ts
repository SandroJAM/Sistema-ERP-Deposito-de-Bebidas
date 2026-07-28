import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/admin.guard';
import { ShellComponent } from './layout/shell/shell.component';
import { LoginComponent } from './features/auth/login/login.component';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { ProdutosComponent } from './features/produtos/produtos.component';
import { CategoriasComponent } from './features/categorias/categorias.component';
import { ClientesComponent } from './features/clientes/clientes.component';
import { VendasComponent } from './features/vendas/vendas.component';
import { FornecedoresComponent } from './features/fornecedores/fornecedores.component';
import { NotasEntradaComponent } from './features/notas-entrada/notas-entrada.component';
import { UsuariosComponent } from './features/usuarios/usuarios.component';
import { ConsultaNotasEntradaComponent } from './features/consultas/consulta-notas-entrada/consulta-notas-entrada.component';
import { ConsultaVendasComponent } from './features/consultas/consulta-vendas/consulta-vendas.component';
import { ConsultaPagamentosComponent } from './features/consultas/consulta-pagamentos/consulta-pagamentos.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: '',
    component: ShellComponent,
    canActivate: [authGuard],
    children: [
      { path: '', component: DashboardComponent },
      // Cadastros
      { path: 'produtos', component: ProdutosComponent },
      { path: 'categorias', component: CategoriasComponent },
      { path: 'fornecedores', component: FornecedoresComponent },
      { path: 'clientes', component: ClientesComponent },
      { path: 'usuarios', component: UsuariosComponent, canActivate: [adminGuard] },
      // Movimentação
      { path: 'notas-entrada', component: NotasEntradaComponent },
      { path: 'vendas', component: VendasComponent },
      // Consultas
      { path: 'consultas/notas-entrada', component: ConsultaNotasEntradaComponent },
      { path: 'consultas/vendas', component: ConsultaVendasComponent },
      { path: 'consultas/pagamentos', component: ConsultaPagamentosComponent },
    ],
  },
  { path: '**', redirectTo: '' },
];

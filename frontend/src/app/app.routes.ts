import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { ShellComponent } from './layout/shell/shell.component';
import { LoginComponent } from './features/auth/login/login.component';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { ProdutosComponent } from './features/produtos/produtos.component';
import { CategoriasComponent } from './features/categorias/categorias.component';
import { ClientesComponent } from './features/clientes/clientes.component';
import { VendasComponent } from './features/vendas/vendas.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: '',
    component: ShellComponent,
    canActivate: [authGuard],
    children: [
      { path: '', component: DashboardComponent },
      { path: 'produtos', component: ProdutosComponent },
      { path: 'categorias', component: CategoriasComponent },
      { path: 'clientes', component: ClientesComponent },
      { path: 'vendas', component: VendasComponent },
    ],
  },
  { path: '**', redirectTo: '' },
];

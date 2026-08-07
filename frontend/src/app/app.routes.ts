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
import { PagamentosComponent } from './features/pagamentos/pagamentos.component';
import { UsuariosComponent } from './features/usuarios/usuarios.component';
import { EmpresaComponent } from './features/empresa/empresa.component';
import { ConsultaNotasEntradaComponent } from './features/consultas/consulta-notas-entrada/consulta-notas-entrada.component';
import { ConsultaVendasComponent } from './features/consultas/consulta-vendas/consulta-vendas.component';
import { ConsultaPagamentosComponent } from './features/consultas/consulta-pagamentos/consulta-pagamentos.component';
import { VasilhamesComponent } from './features/vasilhames/vasilhames.component';
import { DashboardVendasComponent } from './features/relatorios/dashboard-vendas/dashboard-vendas.component';
import { FinanceiroComponent } from './features/relatorios/financeiro/financeiro.component';

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
      { path: 'empresa', component: EmpresaComponent, canActivate: [adminGuard] },
      // Movimentação
      { path: 'notas-entrada', component: NotasEntradaComponent },
      { path: 'vendas', component: VendasComponent },
      { path: 'pagamentos', component: PagamentosComponent },
      { path: 'vasilhames', component: VasilhamesComponent },
      // Relatórios
      { path: 'relatorios/vendas', component: DashboardVendasComponent },
      { path: 'relatorios/financeiro', component: FinanceiroComponent },
      // Consultas
      { path: 'consultas/notas-entrada', component: ConsultaNotasEntradaComponent },
      { path: 'consultas/vendas', component: ConsultaVendasComponent },
      { path: 'consultas/pagamentos', component: ConsultaPagamentosComponent },
    ],
  },
  { path: '**', redirectTo: '' },
];

# Frontend — Controle de Vendas

SPA em Angular.

## Próximo passo

Gerar o projeto com o Angular CLI dentro desta pasta:

```bash
npx @angular/cli new . --routing --style=scss --skip-git
```

## Estrutura sugerida de módulos/features

```
src/app/
├── core/          → serviços singleton, interceptors, guards
├── shared/        → componentes/pipes reutilizáveis
├── features/
│   ├── auth/
│   ├── produtos/
│   ├── categorias/
│   ├── clientes/
│   └── vendas/
└── app-routing.module.ts
```

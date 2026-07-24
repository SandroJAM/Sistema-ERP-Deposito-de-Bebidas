# Frontend — Controle de Vendas de Bebidas

SPA em Angular 18 (standalone components, sem NgModules).

## Como rodar

```bash
npm install
npm start
```

A aplicação sobe em `http://localhost:4200`. Ela espera o back-end rodando em
`http://localhost:8080` (configurável em `src/environments/environment.ts`).

> Este projeto foi montado à mão (sem passar pelo `ng new`), então rode `npm install`
> antes do primeiro `npm start` para baixar as dependências do `package.json`.

## O que já está pronto

- **Login** (`/login`) — autentica contra `POST /api/auth/login` e guarda o token JWT.
- **Guarda de rotas** (`authGuard`) — sem login, qualquer rota redireciona para `/login`.
- **Interceptor de autenticação** — anexa `Authorization: Bearer <token>` em toda chamada HTTP automaticamente.
- **Interceptor de erro** — se a API responder 401 (token expirado/inválido), desloga e manda pro login sozinho.
- **Menu lateral** com as seções: Início, Produtos, Categorias, Clientes, Vendas.
- Estrutura de pastas por feature (`core/`, `layout/`, `features/`).

## Estrutura

```
src/app/
├── core/
│   ├── services/auth.service.ts       → login, logout, token
│   ├── guards/auth.guard.ts           → protege rotas
│   ├── interceptors/                  → token JWT + tratamento de 401
│   └── models/auth.model.ts
├── layout/
│   └── shell/                         → menu lateral + área de conteúdo
├── features/
│   ├── auth/login/                    → tela de login
│   ├── dashboard/                     → página inicial
│   ├── produtos/                      → placeholder (próxima etapa)
│   ├── categorias/                    → placeholder (próxima etapa)
│   ├── clientes/                      → placeholder (próxima etapa)
│   └── vendas/                        → placeholder (próxima etapa)
├── app.routes.ts
├── app.config.ts
└── app.component.ts
```

## Próximos passos sugeridos

- [ ] Tela de Produtos: listar/criar/editar/excluir (consumindo `/api/produtos`)
- [ ] Tela de Categorias
- [ ] Tela de Clientes
- [ ] Tela de Vendas: montar venda com itens e ver total calculado
- [ ] Tratamento de loading/erro padronizado (ex: toasts)
- [ ] Paginação nas listagens, se o volume de dados crescer

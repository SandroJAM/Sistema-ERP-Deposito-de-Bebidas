# Controle de Vendas de Bebidas

Sistema de gestão de vendas de bebidas e outros produtos.

## Arquitetura

Este é um monorepo contendo:

```
controle-vendas-bebidas/
├── backend/     → API REST em Spring Boot
├── frontend/    → SPA em Angular
├── docs/        → Documentação (modelo de dados, decisões)
└── postman/     → Coleções e ambientes do Postman
```

## Stack

| Camada     | Tecnologia         |
|------------|---------------------|
| Back-end   | Java + Spring Boot   |
| Front-end  | Angular              |
| Banco      | (definir: PostgreSQL / MySQL) |
| API client | Postman              |

## Como rodar o projeto

### Back-end
```bash
cd backend
./mvnw spring-boot:run
```
A API sobe por padrão em `http://localhost:8080`.

### Front-end
```bash
cd frontend
npm install
ng serve
```
A aplicação sobe por padrão em `http://localhost:4200`.

## Documentação

- [Modelo de dados](docs/modelo-dados.md)

## Status do projeto

🚧 Em desenvolvimento inicial.

- [x] Definição da stack
- [x] Modelo de dados inicial
- [ ] Scaffold do back-end (Spring Initializr)
- [ ] Scaffold do front-end (Angular CLI)
- [ ] Autenticação (login simples)
- [ ] CRUD de Produto / Categoria
- [ ] CRUD de Cliente
- [ ] Fluxo de Venda (registro + baixa de estoque)
- [ ] Coleção Postman documentada

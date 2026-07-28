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

| Camada     | Tecnologia             |
| ---------- | ------------------------ |
| Back-end   | Java + Spring Boot        |
| Front-end  | Angular                    |
| Banco      | PostgreSQL                  |
| Auth       | JWT (Spring Security)        |
| API client | Postman                       |

## Como rodar o projeto

### Back-end

```bash
cd backend
cp src/main/resources/application-local.properties.example src/main/resources/application-local.properties
# ajuste usuário/senha do seu Postgres no arquivo acima (ou defina a variável DB_PASSWORD)
createdb controle_vendas_bebidas
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

🚧 Em desenvolvimento.

- [x] Definição da stack
- [x] Modelo de dados inicial
- [x] Scaffold do back-end (Spring Initializr)
- [x] Scaffold do front-end (Angular CLI)
- [x] Autenticação (login com JWT)
- [x] CRUD de Categoria / Produto
- [x] CRUD de Cliente
- [x] CRUD de Fornecedor
- [x] Fluxo de Venda (registro + baixa de estoque)
- [ ] Coleção Postman documentada
- [ ] Migrations (Flyway) para produção
- [ ] Perfis de acesso (admin vs. vendedor)

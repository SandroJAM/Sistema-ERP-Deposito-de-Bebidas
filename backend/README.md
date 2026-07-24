# Backend — Controle de Vendas de Bebidas

API REST em Spring Boot 3 (Java 17) + PostgreSQL.

## Como rodar

1. Crie o banco: `createdb controle_vendas_bebidas` (ou via pgAdmin/psql)
2. Copie `src/main/resources/application-local.properties.example` para
   `src/main/resources/application-local.properties` e ajuste usuário/senha do seu Postgres.
3. Rode com o profile local:
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
   ```
   (ou apenas `./mvnw spring-boot:run` se as credenciais padrão do `application.properties` já baterem com seu ambiente)
4. A API sobe em `http://localhost:8080`.

> Este projeto ainda não tem o Maven Wrapper (`mvnw`) gerado. Se não tiver o Maven instalado localmente, rode `mvn -N io.takari:maven:wrapper` uma vez na raiz do projeto, ou abra o projeto na sua IDE (IntelliJ/VS Code), que ele resolve as dependências direto do `pom.xml`.

## O que já está pronto

- Entidades JPA completas: `Usuario`, `Categoria`, `Produto`, `Cliente`, `Venda`, `ItemVenda`
- Repositórios (`JpaRepository`) para todas as entidades
- CRUD completo (controller + service + DTO) de referência para `Categoria` e `Produto`
- Tratamento global de exceções (404 e erros de validação)
- CORS liberado para `http://localhost:4200` (Angular local)

## Endpoints disponíveis

| Método | Rota                  | Descrição            |
|--------|-----------------------|-----------------------|
| GET    | /api/categorias       | Lista categorias      |
| GET    | /api/categorias/{id}  | Busca por id          |
| POST   | /api/categorias       | Cria categoria        |
| PUT    | /api/categorias/{id}  | Atualiza categoria    |
| DELETE | /api/categorias/{id}  | Remove categoria      |
| GET    | /api/produtos         | Lista produtos        |
| GET    | /api/produtos/{id}    | Busca por id          |
| POST   | /api/produtos         | Cria produto          |
| PUT    | /api/produtos/{id}    | Atualiza produto      |
| DELETE | /api/produtos/{id}    | Remove produto        |

## Próximos passos sugeridos

- [ ] CRUD de `Cliente` (mesmo padrão de Categoria/Produto)
- [ ] Fluxo de `Venda`: criar venda com itens, calcular `valorTotal`, abater `estoqueAtual` do produto
- [ ] Autenticação (`Usuario`): endpoint de login + Spring Security + hash de senha (BCrypt)
- [ ] Migrations com Flyway (em vez de `ddl-auto=update`)
- [ ] Testes unitários dos services

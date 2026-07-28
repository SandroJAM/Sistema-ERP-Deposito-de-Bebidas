# Backend — Controle de Vendas de Bebidas

API REST em Spring Boot 3 (Java 17) + PostgreSQL, com autenticação JWT.

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

- Entidades JPA completas: `Usuario`, `Categoria`, `Produto`, `Fornecedor`, `Cliente`, `Venda`, `ItemVenda`
- Repositórios (`JpaRepository`) para todas as entidades
- CRUD completo (controller + service + DTO) para Categoria, Produto, Cliente, Fornecedor e Usuario
- Fluxo de Venda: criação com itens, baixa de `estoqueAtual` do produto e cancelamento
- Autenticação JWT (Spring Security): login em `/api/auth/login`, filtro que valida o token em cada requisição
- Preço de custo (`precoCusto`) no Produto — uso interno, não exposto fora do CRUD
- Tratamento global de exceções (404, estoque insuficiente, erros de validação)
- Senha do banco via variável de ambiente (`DB_PASSWORD`), sem credenciais hardcoded
- CORS liberado para `http://localhost:4200` (Angular local)

## Endpoints disponíveis

### Autenticação

| Método | Rota              | Descrição                          | Autenticado |
|--------|-------------------|-------------------------------------|:-----------:|
| POST   | /api/auth/login   | Login (email + senha) → token JWT   | Não          |

### Categorias

| Método | Rota                  | Descrição            |
|--------|-----------------------|-----------------------|
| GET    | /api/categorias       | Lista categorias      |
| GET    | /api/categorias/{id}  | Busca por id          |
| POST   | /api/categorias       | Cria categoria        |
| PUT    | /api/categorias/{id}  | Atualiza categoria    |
| DELETE | /api/categorias/{id}  | Remove categoria      |

### Produtos

| Método | Rota                | Descrição       |
|--------|---------------------|-------------------|
| GET    | /api/produtos       | Lista produtos    |
| GET    | /api/produtos/{id}  | Busca por id      |
| POST   | /api/produtos       | Cria produto      |
| PUT    | /api/produtos/{id}  | Atualiza produto  |
| DELETE | /api/produtos/{id}  | Remove produto    |

### Clientes

| Método | Rota                | Descrição       |
|--------|---------------------|-------------------|
| GET    | /api/clientes       | Lista clientes    |
| GET    | /api/clientes/{id}  | Busca por id      |
| POST   | /api/clientes       | Cria cliente      |
| PUT    | /api/clientes/{id}  | Atualiza cliente  |
| DELETE | /api/clientes/{id}  | Remove cliente    |

### Fornecedores

| Método | Rota                    | Descrição                                  |
|--------|-------------------------|-----------------------------------------------|
| GET    | /api/fornecedores       | Lista fornecedores (filtro opcional `?nome=`) |
| GET    | /api/fornecedores/{id}  | Busca por id                                  |
| POST   | /api/fornecedores       | Cria fornecedor                               |
| PUT    | /api/fornecedores/{id}  | Atualiza fornecedor                           |
| DELETE | /api/fornecedores/{id}  | Remove fornecedor                             |

### Usuários

| Método | Rota              | Descrição       |
|--------|-------------------|-------------------|
| GET    | /api/usuarios     | Lista usuários    |
| GET    | /api/usuarios/{id}| Busca por id      |
| POST   | /api/usuarios     | Cria usuário      |

### Vendas

| Método | Rota                     | Descrição                                  |
|--------|--------------------------|-----------------------------------------------|
| GET    | /api/vendas              | Lista vendas                                  |
| GET    | /api/vendas/{id}         | Busca por id                                  |
| POST   | /api/vendas              | Cria venda (itens + baixa de estoque)         |
| POST   | /api/vendas/{id}/cancelar| Cancela uma venda                             |

> Todas as rotas acima (exceto `/api/auth/login`) exigem o header `Authorization: Bearer <token>`.

## Próximos passos sugeridos

- [ ] Migrations com Flyway (em vez de `ddl-auto=update`)
- [ ] Perfis de acesso (admin vs. vendedor) validando o campo `perfil` do `Usuario`
- [ ] Testes unitários dos services
- [ ] Coleção Postman documentada cobrindo todos os endpoints acima

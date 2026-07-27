# Modelo de dados

## Entidades

### Usuario
Responsável por autenticação simples (login) no sistema.

| Campo   | Tipo    |
|---------|---------|
| id      | Long (PK) |
| nome    | String  |
| email   | String  |
| senha   | String  |
| perfil  | String  |

### Categoria
Agrupa os produtos (ex: bebidas, salgados).

| Campo | Tipo |
|-------|------|
| id    | Long (PK) |
| nome  | String |

### Fornecedor
Empresas ou pessoas que fornecem os produtos do estoque.

| Campo     | Tipo |
|-----------|------|
| id        | Long (PK) |
| nome      | String |
| cnpj_cpf  | String (opcional) |
| telefone  | String (opcional) |
| email     | String (opcional) |
| ativo     | Boolean |

### Produto

| Campo          | Tipo    |
|----------------|---------|
| id             | Long (PK) |
| categoria_id   | Long (FK → Categoria) |
| fornecedor_id  | Long (FK → Fornecedor, opcional) |
| nome           | String  |
| unidade        | String  |
| preco          | Decimal |
| preco_custo    | Decimal (uso interno) |
| estoque_atual  | Integer |
| ativo          | Boolean |

> `preco_custo` é de uso interno: fica visível e editável apenas na tela de CRUD de Produto — não aparece em vendas, no dashboard nem em nenhum outro lugar do sistema.

### Cliente
Opcional em cada venda.

| Campo    | Tipo |
|----------|------|
| id       | Long (PK) |
| nome     | String |
| telefone | String |

### Venda

| Campo        | Tipo |
|--------------|------|
| id           | Long (PK) |
| usuario_id   | Long (FK → Usuario) |
| cliente_id   | Long (FK → Cliente, opcional) |
| data_venda   | DateTime |
| status       | String |
| valor_total  | Decimal |

### ItemVenda

| Campo           | Tipo |
|-----------------|------|
| id              | Long (PK) |
| venda_id        | Long (FK → Venda) |
| produto_id      | Long (FK → Produto) |
| quantidade      | Integer |
| preco_unitario  | Decimal |
| subtotal        | Decimal |

## Relacionamentos

- Um `Usuario` registra várias `Venda`.
- Um `Cliente` pode realizar várias `Venda` (opcional).
- Uma `Venda` contém vários `ItemVenda`.
- Um `Produto` pode aparecer em vários `ItemVenda`.
- Uma `Categoria` classifica vários `Produto`.
- Um `Fornecedor` pode fornecer vários `Produto` (vínculo opcional).

## Regras de negócio iniciais

1. Ao fechar uma `Venda`, o `estoque_atual` de cada `Produto` envolvido é abatido pela `quantidade` do respectivo `ItemVenda`.
2. `valor_total` da venda é a soma dos `subtotal` de todos os itens.
3. `cliente_id` é opcional (permite venda de balcão sem identificação do cliente).

## Evoluções futuras (não implementadas na v1)

- `MovimentacaoEstoque` para rastrear entradas/reposição.
- `FormaPagamento` associada à venda.
- Perfis de acesso (admin vs. vendedor) usando o campo `perfil`.

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

| Campo             | Tipo |
|-------------------|------|
| id                | Long (PK) |
| usuario_id        | Long (FK → Usuario) |
| cliente_id        | Long (FK → Cliente, opcional) |
| data_venda        | DateTime |
| status            | String (`ABERTA`, `FINALIZADA`, `CANCELADA`) |
| valor_total       | Decimal |
| status_pagamento  | String (`PENDENTE`, `PARCIAL`, `PAGO`) |

> `status` é o ciclo de vida da venda em si; `status_pagamento` é a situação de quitação perante o cliente — independente e calculado a partir da soma dos `RecebimentoVenda`. Toda venda finalizada nasce `PENDENTE`.

### RecebimentoVenda
Baixa (total ou parcial) de uma venda feita a um cliente — o equivalente, do lado do recebimento, ao `Pagamento` do lado do fornecedor.

| Campo             | Tipo |
|-------------------|------|
| id                | Long (PK) |
| venda_id          | Long (FK → Venda) |
| data_recebimento  | Date |
| valor             | Decimal |
| observacao        | String (opcional) |

### Empresa
Dados cadastrais do próprio depósito — pensado pra ter um único registro. Nome/endereço/telefone aparecem no topo de todas as telas e no cabeçalho do histórico/PDF de vendas.

| Campo     | Tipo |
|-----------|------|
| id        | Long (PK) |
| nome      | String (obrigatório) |
| endereco  | String (opcional) |
| telefone  | String (opcional) |

### ItemVenda

| Campo           | Tipo |
|-----------------|------|
| id              | Long (PK) |
| venda_id        | Long (FK → Venda) |
| produto_id      | Long (FK → Produto) |
| quantidade      | Integer |
| preco_unitario  | Decimal |
| subtotal        | Decimal |

### NotaEntrada
Nota fiscal de entrada de mercadorias no estoque.

| Campo         | Tipo |
|---------------|------|
| id            | Long (PK) |
| numero        | String |
| fornecedor_id | Long (FK → Fornecedor, opcional) |
| data_nota     | Date |
| valor_nota    | Decimal |
| vencimento    | Date |
| status        | String (`ATIVA`, `CANCELADA`) |

### ItemNotaEntrada

| Campo            | Tipo |
|------------------|------|
| id               | Long (PK) |
| nota_entrada_id  | Long (FK → NotaEntrada) |
| produto_id       | Long (FK → Produto) |
| quantidade       | Integer |
| valor_unitario   | Decimal |
| subtotal         | Decimal |

### Pagamento
Parcela a pagar, gerada automaticamente ao inserir uma `NotaEntrada`.

| Campo            | Tipo |
|------------------|------|
| id               | Long (PK) |
| nota_entrada_id  | Long (FK → NotaEntrada) |
| numero_fatura    | String (= `numero` da NotaEntrada de origem) |
| numero_parcela   | Integer |
| data_emissao     | Date |
| valor_a_pagar    | Decimal |
| data_vencimento  | Date |
| status           | String (`PENDENTE`, `PAGO`, `CANCELADO`) |

## Relacionamentos

- Um `Usuario` registra várias `Venda`.
- Um `Cliente` pode realizar várias `Venda` (opcional).
- Uma `Venda` contém vários `ItemVenda`.
- Um `Produto` pode aparecer em vários `ItemVenda`.
- Uma `Categoria` classifica vários `Produto`.
- Um `Fornecedor` pode fornecer vários `Produto` (vínculo opcional).
- Um `Fornecedor` pode emitir várias `NotaEntrada` (vínculo opcional).
- Uma `NotaEntrada` contém vários `ItemNotaEntrada` e gera um ou mais `Pagamento`.
- Um `Produto` pode aparecer em vários `ItemNotaEntrada`.
- Uma `Venda` pode receber vários `RecebimentoVenda` (baixas parciais) até ficar quitada.

## Regras de negócio iniciais

1. Ao fechar uma `Venda`, o `estoque_atual` de cada `Produto` envolvido é abatido pela `quantidade` do respectivo `ItemVenda`.
2. `valor_total` da venda é a soma dos `subtotal` de todos os itens.
3. `cliente_id` é opcional (permite venda de balcão sem identificação do cliente).
4. Ao inserir uma `NotaEntrada`, o `estoque_atual` de cada `Produto` envolvido é somado pela `quantidade` do respectivo `ItemNotaEntrada`.
5. A soma dos `subtotal` de todos os `ItemNotaEntrada` deve ser exatamente igual a `valor_nota`; havendo divergência, a nota é rejeitada e nada é gravado.
6. Ao inserir uma `NotaEntrada`, é gerado automaticamente 1 `Pagamento` à vista (`numero_parcela = 1`), com `valor_a_pagar = valor_nota` e `data_vencimento = vencimento` da nota. O usuário pode posteriormente editar essa parcela ou dividi-la em mais de uma — o modelo já suporta múltiplos `Pagamento` com o mesmo `numero_fatura`.
7. Ao cancelar uma `NotaEntrada`, o estoque de cada produto é revertido e **todas** as parcelas de `Pagamento` são marcadas como `CANCELADO` — inclusive as que já estavam `PAGO`.
8. Um `RecebimentoVenda` não pode ultrapassar o saldo devedor da `Venda` (`valor_total` − soma dos recebimentos já lançados), nem ser lançado em venda `CANCELADA`.
9. `status_pagamento` da `Venda` é recalculado a cada `RecebimentoVenda`: `PENDENTE` (nada recebido), `PARCIAL` (recebido &lt; total) ou `PAGO` (recebido ≥ total).
10. Venda `CANCELADA` tem saldo devedor sempre zero, independentemente de `status_pagamento`.

## Evoluções futuras (não implementadas na v1)

- `MovimentacaoEstoque` para rastrear entradas/reposição de forma unificada (hoje só via `NotaEntrada`).
- Edição/divisão de parcelas de `Pagamento` diretamente pela UI (hoje só a criação automática da 1ª parcela está implementada no back-end).
- `FormaPagamento` associada à venda e ao pagamento.
- Perfis de acesso (admin vs. vendedor) usando o campo `perfil`.

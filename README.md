# 🔢 Sudoku — Jogo no Terminal (Java)

> Implementação de um jogo de **Sudoku** interativo, executado diretamente no terminal, desenvolvido em Java com foco em boas práticas de orientação a objetos.

---

## 📋 Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Como Funciona](#como-funciona)
- [Pré-requisitos](#pré-requisitos)
- [Como Executar](#como-executar)
- [Formato dos Argumentos](#formato-dos-argumentos)
- [Exemplo de Execução](#exemplo-de-execução)
- [Melhorias Implementadas](#melhorias-implementadas)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Contribuindo](#contribuindo)

---

## 📖 Sobre o Projeto

Este projeto implementa o clássico jogo **Sudoku** para ser jogado no terminal. O puzzle é configurado via argumentos de linha de comando, e o jogador interage com um menu de opções para inserir/remover números, visualizar o tabuleiro e verificar o status da partida.

O objetivo do jogo é preencher o tabuleiro 9×9 com números de 1 a 9, respeitando as regras:
- Cada número deve aparecer apenas **uma vez por linha**
- Cada número deve aparecer apenas **uma vez por coluna**
- Cada número deve aparecer apenas **uma vez por região 3×3**

---

## 🕹 Como Funciona

O menu interativo oferece as seguintes opções:

| Opção | Ação |
|-------|------|
| 1 | Iniciar um novo jogo |
| 2 | Inserir um número em uma posição |
| 3 | Remover um número de uma posição |
| 4 | Visualizar o tabuleiro atual |
| 5 | Verificar status do jogo (erros, progresso) |
| 6 | Limpar o jogo (mantém os valores fixos) |
| 7 | Finalizar o jogo (valida se está completo) |
| 8 | Sair |

O tabuleiro é exibido visualmente no terminal com separação das regiões 3×3:

```
*************************************************************************************
*|---0---||---1---||---2---|*|---3---||---4---||---5---|*|---6---||---7---||---8---|*
...
0|   5   ||   3   ||       |*|       ||   7   ||       |*|       ||       ||       |0
...
```

---

## 🛠 Pré-requisitos

- Java JDK 11 ou superior
- Terminal com suporte a UTF-8 (para exibição correta de caracteres acentuados)

---

## ▶️ Como Executar

### 1. Compilar

```bash
javac -d out/production/sudoku \
  src/br/com/dio/model/GameStatusEnum.java \
  src/br/com/dio/model/Space.java \
  src/br/com/dio/model/Board.java \
  src/br/com/dio/util/BoardTemplate.java \
  src/br/com/dio/Main.java
```

### 2. Executar

```bash
java -cp out/production/sudoku br.com.dio.Main \
  "0,0;5,true" "0,1;3,true" "0,2;0,false" \
  ...
```

> **Dica:** Monte um script `.sh` com todos os 81 argumentos para facilitar a execução.

---

## 📐 Formato dos Argumentos

Cada argumento representa **uma célula** do tabuleiro e segue o formato:

```
col,row;expected,fixed
```

| Campo      | Descrição |
|------------|-----------|
| `col`      | Índice da coluna (0–8) |
| `row`      | Índice da linha  (0–8) |
| `expected` | Valor correto da solução (1–9) |
| `fixed`    | `true` se é uma célula pré-preenchida do puzzle; `false` se o jogador deve preencher |

**Exemplo:** `"2,5;7,false"` → coluna 2, linha 5, solução = 7, célula editável.

São necessários exatamente **81 argumentos** (um para cada célula do tabuleiro 9×9).

---

## 📸 Exemplo de Execução

```
Selecione uma das opções a seguir
1 - Iniciar um novo jogo
2 - Colocar um novo número
...
> 4
Seu jogo se encontra da seguinte forma:
*************************************************************************************
*|---0---||---1---||---2---|*|---3---||---4---||---5---|*|---6---||---7---||---8---|*
*|       ||       ||       |*|       ||       ||       |*|       ||       ||       |*
0|   5   ||   3   ||       |*|       ||   7   ||       |*|       ||       ||       |0
*|       ||       ||       |*|       ||       ||       |*|       ||       ||       |*
*|-------||-------||-------|*|-------||-------||-------|*|-------||-------||-------|*
*|-------||-------||-------|*|-------||-------||-------|*|-------||-------||-------|*
*|       ||       ||       |*|       ||       ||       |*|       ||       ||       |*
1|   6   ||       ||       |*|   1   ||   9   ||   5   |*|       ||       ||       |1
*|       ||       ||       |*|       ||       ||       |*|       ||       ||       |*
*|-------||-------||-------|*|-------||-------||-------|*|-------||-------||-------|*
...
*************************************************************************************
```

---

## 🔧 Melhorias Implementadas

Em relação ao código original, as seguintes melhorias foram aplicadas:

### 1. Correção de bug no parsing dos argumentos (`Main.java`)
O código original realizava um segundo `split(",")` sobre o valor já separado por `";"`, o que poderia gerar `ArrayIndexOutOfBoundsException`. O parsing foi corrigido para usar corretamente os índices do array resultante do `split(",")`:
```java
// Antes (bugado)
var expected = Integer.parseInt(positionConfig.split(",")[0]);
var fixed    = Boolean.parseBoolean(positionConfig.split(",")[1]);

// Depois (correto)
var parts    = positionConfig.split(",");
var expected = Integer.parseInt(parts[0]);
var fixed    = Boolean.parseBoolean(parts[1]);
```

### 2. Correção de typos nas mensagens ao usuário
Erros de digitação corrigidos:
- `"iniciado iniciado"` → `"iniciado"`
- `"conté, erros"` → `"contém erros"`
- `"preenhcer"` → `"preencher"`

### 3. Mensagens corrigidas em `removeNumber()`
As mensagens exibiam "número será **inserido**" ao remover um número. Corrigido para "número será **removido**".

### 4. `clearGame()` aceita "nao" sem acento
A confirmação agora aceita `"nao"` além de `"não"`, evitando travamento em terminais que não lidam bem com caracteres acentuados em input.

### 5. `Board.reset()` preserva células fixas
No original, `reset()` chamava `clearSpace()` em **todas** as células, incluindo as fixas. Como `Space.setActual()` já ignora silenciosamente células fixas, a correção foi adicionar um filtro explícito para deixar a intenção clara:
```java
flatSpaces()
    .filter(s -> !s.isFixed())
    .forEach(Space::clearSpace);
```

### 6. `Board.getSpaces()` retorna lista não modificável
O getter agora retorna `Collections.unmodifiableList(spaces)`, protegendo a estrutura interna do tabuleiro contra modificações externas acidentais.

### 7. Método auxiliar `flatSpaces()` extraído em `Board`
A expressão `spaces.stream().flatMap(Collection::stream)` era repetida em três métodos. Extraída para um método privado, eliminando duplicação.

### 8. Campo `label` em `GameStatusEnum` declarado como `final`
O campo era mutável sem necessidade. Tornado `final` para garantir imutabilidade do enum.

### 9. `Space.toString()` adicionado
Facilita debugging e logging do estado de células individuais.

### 10. `Scanner` fechado corretamente
O `Scanner` agora é fechado tanto na opção "Sair" quanto via `ShutdownHook`, prevenindo vazamento de recurso.

### 11. Constante `BOARD_TEMPLATE` tornada `public static final` (removido `final` redundante)
O modificador `public final static` foi reordenado para a convenção Java padrão `public static final`.

### 12. Javadoc adicionado nas classes e métodos principais
Documentação inline para facilitar a compreensão do domínio e do formato dos argumentos.

---

## 📁 Estrutura do Projeto

```
sudoku/
└── src/
    └── br/com/dio/
        ├── Main.java                  ← menu interativo e lógica de entrada
        ├── model/
        │   ├── Board.java             ← tabuleiro: lógica de jogo e validação
        │   ├── GameStatusEnum.java    ← enum de status (não iniciado / incompleto / completo)
        │   └── Space.java             ← célula individual do tabuleiro
        └── util/
            └── BoardTemplate.java     ← template ASCII para renderização no terminal
```

---

## 🤝 Contribuindo

Contribuições são bem-vindas!

- ⭐ Deixe uma estrela se o projeto te ajudou
- 🐛 Abra uma *issue* para reportar problemas
- 🔀 Faça um *fork* e envie um *pull request* com melhorias

---

*Projeto baseado no código original de [José Luiz Abreu Cardoso Junior](https://github.com/juniorjrjl), disponibilizado como desafio prático na plataforma [Digital Innovation One](https://web.digitalinnovation.one/).*

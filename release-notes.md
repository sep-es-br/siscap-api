# SISCAP
## Versão 1.1.0
### Feature de Programas

**Aplicação**
1. Permissionamento e autorização de rotas
   * Adiciona permissionamento para Programas.
   * Adiciona autorização para rotas de Programas.
2. Classes auxiliares
   * Adiciona entidade `ControleHistorico`: Superclasse extendida por entidades a fim de centralizar lógica de controle de registro de manipulação de dados (criação, edição e deleção).
   * Adiciona enums para melhoria da legibilidade e manutenção do código (ex: `EquipeEnum`, `MoedaEnum`, `PapelEnum`, etc.).

**Programas**
1. Fluxo principal de Programas
   * Adiciona entidade `Programa`.
   * Adiciona classes para o fluxo de Programas (Controller, Service, Repository, DTOs, etc.).
   * Adiciona entidades assosciativas `ProgramaPessoa`, `ProgramaProjeto` e `ProgramaValor`, além de suas classes pertinentes.

**Projetos**
1. Entidade
   * Refatora classe para simplificar e modularizar métodos.
   * Adiciona propriedade _data_registro_.
   * Terceiriza lógica de controle de registro para superclasse `ControleHistorico`.
2. Rateio
   * Adiciona entidades `ProjetoMicrorregiao` e `ProjetoCidade`, além de suas classes pertinentes.
   * Adiciona serviço `ProjetoRateioService`, centralizando nele as lógicas de CRUD de `ProjetoMicrorregiao` e `ProjetoCidade`.
   * Substitui lógica da propriedade _microrregioes_ da entidade por Rateio (`ProjetoMicrorregiao` e `ProjetoCidade`)
3. Equipe de Elaboração
   * Adiciona entidade `ProjetoPessoa` e suas classes pertinentes.
   * Substitui lógica da propriedade _equipeElaboracao_ da entidade por `ProjetoPessoa`

**Pessoas**
1. Entidade
   * Refatora classe para simplificar e modularizar métodos.
   * Terceiriza lógica de controle de registro para superclasse `ControleHistorico`.
2. PessoaOrganizacao
   * Adiciona entidade `PessoaOrganizacao` e suas classes pertinentes.
   * Centraliza nela a lógica do relacionamento entre entidades `Pessoa` e `Organização`

**Organizações**
1. Entidade
   * Refatora classe para simplificar e modularizar métodos.
   * Terceiriza lógica de controle de registro para superclasse `ControleHistorico`.
2. PessoaOrganizacao
   * Adiciona entidade `PessoaOrganizacao` e suas classes pertinentes.
   * Centraliza nela a lógica do relacionamento entre entidades `Pessoa` e `Organização`

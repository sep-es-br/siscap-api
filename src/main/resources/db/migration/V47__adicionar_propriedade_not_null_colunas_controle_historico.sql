-- 27/09/2024 - Alteração colunas controle histórico ("criado_em", "atualizado_em", "apagado")
-- Adiciona propriedade "not null" para colunas "criado_em" e "apagado" nas tabelas do sistema

-- Tabela "area"
alter table area
    alter column criado_em set not null;
alter table area
    alter column criado_em set default current_timestamp;
alter table area
    alter column apagado set not null;
alter table area
    alter column apagado set default false;

-- Tabela "cidade"
-- Insere data de criação nas cidades
update cidade
set criado_em = timestamp '2024-03-12 14:07:00.000'
where criado_em is null;

alter table cidade
    alter column criado_em set not null;
alter table cidade
    alter column criado_em set default current_timestamp;
alter table cidade
    alter column apagado set not null;
alter table cidade
    alter column apagado set default false;

-- Tabela "eixo"
-- |-> 'default' de 'apagado' já possui valor 'false'
alter table eixo
    alter column criado_em set not null;
alter table eixo
    alter column criado_em set default current_timestamp;
alter table eixo
    alter column apagado set not null;

-- Tabela "endereco"
-- |-> 'not null' de 'apagado' já possui valor 'true'
-- |-> 'default' de 'apagado' já possui valor 'false'
alter table endereco
    alter column criado_em set not null;
alter table endereco
    alter column criado_em set default current_timestamp;

-- Tabela "equipe"
alter table equipe
    alter column criado_em set not null;
alter table equipe
    alter column criado_em set default current_timestamp;
alter table equipe
    alter column apagado set not null;
alter table equipe
    alter column apagado set default false;

-- Tabela "estado"
-- |-> 'not null' de 'apagado' já possui valor 'true'
-- |-> 'default' de 'apagado' já possui valor 'false'
-- Insere data de criação nos estados
update estado
set criado_em = timestamp '2024-03-12 14:07:00.000'
where criado_em is null;

alter table estado
    alter column criado_em set not null;
alter table estado
    alter column criado_em set default current_timestamp;

-- Tabela "microrregiao"
alter table microrregiao
    alter column criado_em set not null;
alter table microrregiao
    alter column criado_em set default current_timestamp;
alter table microrregiao
    alter column apagado set not null;
alter table microrregiao
    alter column apagado set default false;

-- Tabela "organizacao"
-- |-> 'not null' de 'apagado' já possui valor 'true'
alter table organizacao
    alter column criado_em set not null;
alter table organizacao
    alter column criado_em set default current_timestamp;
alter table organizacao
    alter column apagado set default false;

-- Tabela "pais"
-- |-> 'not null' de 'apagado' já possui valor 'true'
-- Insere data de criação nos países
update pais
set criado_em = timestamp '2024-03-19 17:28:00.000'
where criado_em is null;

alter table pais
    alter column criado_em set not null;
alter table pais
    alter column criado_em set default current_timestamp;
alter table pais
    alter column apagado set default false;

-- Tabela "papel"
alter table papel
    alter column criado_em set not null;
alter table papel
    alter column criado_em set default current_timestamp;
alter table papel
    alter column apagado set not null;
alter table papel
    alter column apagado set default false;

-- Tabela "pessoa"
-- OBS: Algumas entidades possuem "criado_em" com valor NULL; Não mexer por motivos de autenticação Acesso Cidadão
alter table pessoa
    alter column apagado set not null;
alter table pessoa
    alter column apagado set default false;

-- Tabela "pessoa_organizacao"
alter table pessoa_organizacao
    alter column criado_em set not null;
alter table pessoa_organizacao
    alter column criado_em set default current_timestamp;
alter table pessoa_organizacao
    alter column apagado set not null;
alter table pessoa_organizacao
    alter column apagado set default false;

-- Tabela "plano"
-- |-> 'default' de 'apagado' já possui valor 'false'
alter table plano
    alter column criado_em set not null;
alter table plano
    alter column criado_em set default current_timestamp;
alter table plano
    alter column apagado set not null;

-- Tabela "programa"
-- |-> 'not null' de 'apagado' já possui valor 'true'
-- |-> 'not null' de 'criado_em' já possui valor 'true'
alter table programa
    alter column criado_em set default current_timestamp;
alter table programa
    alter column apagado set default false;

-- Tabela "programa_pessoa"
-- |-> 'not null' de 'apagado' já possui valor 'true'
-- |-> 'not null' de 'criado_em' já possui valor 'true'
alter table programa_pessoa
    alter column criado_em set default current_timestamp;
alter table programa_pessoa
    alter column apagado set default false;

-- Tabela "programa_projeto"
-- |-> 'not null' de 'apagado' já possui valor 'true'
-- |-> 'not null' de 'criado_em' já possui valor 'true'
alter table programa_projeto
    alter column criado_em set default current_timestamp;
alter table programa_projeto
    alter column apagado set default false;

-- Tabela "programa_valor"
-- |-> 'not null' de 'apagado' já possui valor 'true'
-- |-> 'not null' de 'criado_em' já possui valor 'true'
alter table programa_valor
    alter column criado_em set default current_timestamp;
alter table programa_valor
    alter column apagado set default false;

-- Tabela "projeto"
-- |-> 'not null' de 'apagado' já possui valor 'true'
alter table projeto
    alter column criado_em set not null;
alter table projeto
    alter column criado_em set default current_timestamp;
alter table projeto
    alter column apagado set default false;

-- Tabela "projeto_cidade"
-- |-> 'not null' de 'apagado' já possui valor 'true'
-- |-> 'not null' de 'criado_em' já possui valor 'true'
alter table projeto_cidade
    alter column criado_em set default current_timestamp;
alter table projeto_cidade
    alter column apagado set default false;

-- Tabela "projeto_microrregiao"
-- |-> 'not null' de 'apagado' já possui valor 'true'
-- |-> 'not null' de 'criado_em' já possui valor 'true'
alter table projeto_microrregiao
    alter column criado_em set default current_timestamp;
alter table projeto_microrregiao
    alter column apagado set default false;

-- Tabela "projeto_pessoa"
alter table projeto_pessoa
    alter column criado_em set not null;
alter table projeto_pessoa
    alter column criado_em set default current_timestamp;
alter table projeto_pessoa
    alter column apagado set not null;
alter table projeto_pessoa
    alter column apagado set default false;

-- Tabela "status"
-- |-> 'not null' de 'apagado' já possui valor 'true'
alter table status
    alter column criado_em set not null;
alter table status
    alter column criado_em set default current_timestamp;
alter table status
    alter column apagado set default false;

-- Tabela "tipo_organizacao"
-- |-> 'not null' de 'apagado' já possui valor 'true'
alter table tipo_organizacao
    alter column criado_em set not null;
alter table tipo_organizacao
    alter column criado_em set default current_timestamp;
alter table tipo_organizacao
    alter column apagado set default false;

-- Tabela "valor"
alter table valor
    alter column criado_em set not null;
alter table valor
    alter column criado_em set default current_timestamp;
alter table valor
    alter column apagado set not null;
alter table valor
    alter column apagado set default false;
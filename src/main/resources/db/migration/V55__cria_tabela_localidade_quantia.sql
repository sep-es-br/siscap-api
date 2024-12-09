-- 23/10/2024
-- Cria tabela associativa localidade_quantia, relacionando o rateio
-- por localidades (microrregiao ou municipio) com um Projeto, alem
-- do tipo de valor (Estimado, Captado, etc.)
create table localidade_quantia
(
    id            serial primary key,
    id_projeto    integer        not null references projeto,
    id_localidade integer        not null references localidade,
    id_tipo_valor integer        not null references tipo_valor,
    quantia       numeric(25, 2) not null,
    moeda         varchar(3)     not null,
    criado_em     timestamp      not null default current_timestamp,
    atualizado_em timestamp,
    apagado       boolean        not null default false
);

-- Remove tabelas anteriormente pertinentes ao rateio
-- (projeto_microrregiao e projeto_cidade)
drop table if exists projeto_microrregiao;
drop table if exists projeto_cidade;

-- Remove tabelas associativas de valor
-- Ideia e centralizar valores em localidade_quantia
drop table if exists projeto_valor;
drop table if exists programa_valor;

-- Altera relacionamento entre Programa e Projeto
-- Adiciona coluna id_programa em Projeto
alter table projeto
    add column id_programa integer references programa;
drop table if exists programa_projeto;

-- Adiciona coluna teto_quantia em Programa
alter table programa
    add column teto_quantia numeric(25, 2);
alter table programa
    add column id_tipo_valor integer references tipo_valor;
alter table programa
    add column moeda varchar(3);
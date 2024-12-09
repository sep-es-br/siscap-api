-- 02/09/2024 - Refatoração Rateio:

-- Deleta registros anteriores á refatoração (não são mais compatíveis)

delete from projeto_cidade proj_cid
where data_inicio < '2024-09-01';

delete from projeto_microrregiao proj_mr
where apagado is null;

-- Tabela "projeto_cidade"
-- |-> Cria coluna "percentual"
-- |-> Adiciona propriedade 'not null' para colunas de controle de histórico

alter table projeto_cidade add column percentual numeric(5, 2);
alter table projeto_cidade alter column percentual set not null;
alter table projeto_cidade alter column quantia set not null;
alter table projeto_cidade alter column data_inicio set not null;
alter table projeto_cidade alter column criado_em set not null;
alter table projeto_cidade alter column apagado set not null;

-- Tabela "projeto_microrregiao"
-- |-> Cria colunas "percentual", "quantia" "data_inicio", "data_fim"
-- |-> Adiciona propriedade 'not null' para colunas "percentual", "quantia" e de controle de histórico

alter table projeto_microrregiao add column percentual numeric(5, 2);
alter table projeto_microrregiao alter column percentual set not null;
alter table projeto_microrregiao add column quantia numeric(25, 2);
alter table projeto_microrregiao alter column quantia set not null;
alter table projeto_microrregiao add column data_inicio timestamp;
alter table projeto_microrregiao alter column data_inicio set not null;
alter table projeto_microrregiao add column data_fim timestamp;
alter table projeto_microrregiao alter column criado_em set not null;
alter table projeto_microrregiao alter column apagado set not null;



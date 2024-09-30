-- 30/09/2024 - Criação tabela "programa_organizacao"; Alteração tabela "programa"; Alteração tabela "valor";

-- Tabela "programa_organizacao"
-- |-> Relacionamento N -> N de Programa e Organização (contexto de "Orgão Executor")
create table programa_organizacao
(
    id_programa    integer not null
        constraint fk_prog_org_id_programa_programa
            references programa (id),
    id_organizacao integer not null
        constraint fk_prog_org_id_organizacao_organizacao
            references organizacao (id)
);

-- Tabela "programa"
-- |-> Remove coluna "orgao_executor" (relacionamento agora mantido na associativa "programa_organizacao")
alter table programa
    drop column if exists orgao_executor;

-- Tabela "valor"
-- |-> Remove colunas "data_inicio" e "data_fim"
alter table valor
    drop column if exists data_inicio;
alter table valor
    drop column if exists data_fim;
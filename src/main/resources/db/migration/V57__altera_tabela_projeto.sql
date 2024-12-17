-- 08/12/2024
-- Alterações tabela "projeto"
-- - Cria coluna "status" varchar(255) not null, valores controlados por enum
-- - Cria coluna "fase" varchar(255) not null, valores controlados por enum (por hora, valor fixo "DIC")
-- - Cria coluna "rascunho" boolean not null
-- - Substitui coluna "id_tipo_status" por "status"

alter table projeto
    add column status   varchar(255) not null default 'Em Análise',
    add column fase     varchar(255) not null default 'DIC',
    add column rascunho boolean      not null default false;
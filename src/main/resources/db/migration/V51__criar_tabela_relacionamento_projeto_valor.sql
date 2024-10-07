-- 03/10/2024 - Criação da tabela de associativa projeto_valor relacionando projeto e valor
-- Ideia é desacoplar valor da tabela projeto (valor_estimado)

create table projeto_valor
(
    id            serial primary key,
    id_projeto    int            not null
        constraint fk_projeto_valor_id_projeto_projeto references projeto (id),
    id_valor      int            not null
        constraint fk_projeto_valor_id_valor_valor references valor (id),
    moeda         varchar(3)     not null,
    quantia       numeric(25, 2) not null,
    data_inicio   timestamp,
    data_fim      timestamp,
    criado_em     timestamp      not null default current_timestamp,
    atualizado_em timestamp,
    apagado       boolean        not null default false
);

-- Mapear propriedade valor_estimado da tabela projeto para a tabela projeto_valor
insert into projeto_valor (id_projeto, id_valor, moeda, quantia, data_inicio, data_fim, criado_em)
select id, 1, 'BRL', valor_estimado, criado_em, null, criado_em
from projeto;

-- -- Remover valor_estimado da tabela projeto
alter table projeto drop column if exists valor_estimado;
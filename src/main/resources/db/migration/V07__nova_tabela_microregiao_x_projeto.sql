alter table projeto
    drop column if exists id_microrregiao;

create table projeto_microrregiao
(
    id             serial
        constraint pk_projeto_microrregiao primary key,
    id_projeto     integer not null
        constraint fk_proj_microreg_id_projeto_projeto references projeto,
    id_microrregiao integer not null
        constraint fk_proj_microreg_id_microrregiao_microrregiao references microrregiao,
    criado_em      timestamp,
    atualizado_em  timestamp,
    apagado        boolean
);
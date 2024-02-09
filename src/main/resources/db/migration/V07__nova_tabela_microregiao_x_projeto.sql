alter table projeto
    drop column if exists id_microregiao;

create table projeto_microregiao
(
    id             serial
        constraint pk_projeto_microregiao primary key,
    id_projeto     integer not null
        constraint fk_proj_microreg_id_projeto_projeto references projeto,
    id_microregiao integer not null
        constraint fk_proj_microreg_id_microregiao_microregiao references microregiao,
    criado_em      timestamp,
    atualizado_em  timestamp,
    apagado        boolean
);
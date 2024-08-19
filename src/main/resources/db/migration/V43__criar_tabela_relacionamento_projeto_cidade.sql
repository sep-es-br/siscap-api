create table projeto_cidade
(
    id            serial
        constraint pk_projeto_cidade
            primary key,
    id_projeto    integer not null
        constraint fk_projeto_cidade_id_projeto_projeto
            references projeto,
    id_cidade     integer not null
        constraint fk_projeto_cidade_id_cidade_cidade
            references cidade,
    quantia       numeric(25, 2),
    data_inicio   timestamp,
    data_fim      timestamp,
    criado_em     timestamp,
    atualizado_em timestamp,
    apagado       boolean
);
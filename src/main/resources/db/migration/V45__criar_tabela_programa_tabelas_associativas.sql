create table programa
(
    id             serial primary key,
    sigla          varchar(12) unique,
    titulo         varchar(150) not null,
    id_status      int          not null
        constraint fk_programa_status_status references status (id),
    orgao_executor int          not null
        constraint fk_programa_orgao_executor_orgao_executor references organizacao (id),
    data_inicio    timestamp    not null,
    data_fim       timestamp,
    criado_em      timestamp    not null,
    atualizado_em  timestamp,
    apagado        boolean      not null
);

create table programa_projeto
(
    id            serial primary key,
    id_programa   int            not null
        constraint fk_programa_projeto_id_programa_programa references programa (id),
    id_projeto    int            not null
        constraint fk_programa_projeto_id_projeto_projeto references projeto (id),
    valor         numeric(25, 2) not null,
    data_inicio   timestamp      not null,
    data_fim      timestamp,
    criado_em     timestamp      not null,
    atualizado_em timestamp,
    apagado       boolean        not null
);

create table programa_valor
(
    id            serial primary key,
    id_programa   int            not null
        constraint fk_programa_valor_id_programa_programa references programa (id),
    id_valor      int            not null
        constraint fk_programa_valor_id_valor_valor references valor (id),
    moeda         varchar(3)     not null,
    quantia       numeric(25, 2) not null,
    data_inicio   timestamp      not null,
    data_fim      timestamp,
    criado_em     timestamp      not null,
    atualizado_em timestamp,
    apagado       boolean        not null
);

create table programa_pessoa
(
    id            serial primary key,
    id_programa   int       not null
        constraint fk_programa_pessoa_id_programa_programa references programa (id),
    id_pessoa     int       not null
        constraint fk_programa_pessoa_id_pessoa_pessoa references pessoa (id),
    id_papel      int       not null
        constraint fk_programa_pessoa_id_papel_papel references papel (id),
    id_equipe     int       not null
        constraint fk_programa_pessoa_id_equipe_equipe references equipe (id),
    id_status     int       not null
        constraint fk_programa_pessoa_id_status_status references status (id),
    data_inicio   timestamp not null,
    data_fim      timestamp,
    criado_em     timestamp not null,
    atualizado_em timestamp,
    apagado       boolean   not null,
    justificativa varchar(255)
);
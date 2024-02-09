create table status
(
    id            integer not null
        constraint pk_status primary key,
    status        varchar not null,
    criado_em     timestamp,
    atualizado_em timestamp,
    apagado       boolean
);

create table pais
(
    id            serial
        constraint pk_pais
            primary key,
    nome          varchar not null,
    continente    varchar not null,
    subcontinente varchar not null,
    iso_alpha_3   varchar not null,
    ddi           varchar not null,
    criado_em     timestamp,
    atualizado_em timestamp,
    apagado       boolean
);

create table estado
(
    id             serial
        constraint pk_estado
            primary key,
    id_pais        integer not null
        constraint fk_estado_id_pais_pais
            references pais,
    id_ibge        integer not null unique,
    id_regiao_ibge integer not null,
    nome           varchar not null,
    sigla          varchar not null unique,
    nome_regiao    varchar not null,
    sigla_regiao   varchar not null,
    criado_em      timestamp,
    atualizado_em  timestamp,
    apagado        boolean
);

create table cidade
(
    id            serial
        constraint pk_cidade
            primary key,
    id_estado     integer not null
        constraint fk_cidade_id_estado_estado
            references estado,
    id_ibge       integer not null unique,
    nome          varchar not null,
    criado_em     timestamp,
    atualizado_em timestamp,
    apagado       boolean
);

create table endereco
(
    id            serial
        constraint pk_endereco
            primary key,
    rua           varchar not null,
    numero        varchar not null,
    bairro        varchar not null,
    complemento   varchar,
    codigo_postal varchar not null,
    id_cidade     integer not null
        constraint fk_endereco_id_cidade_cidade
            references cidade,
    criado_em     timestamp,
    atualizado_em timestamp,
    apagado       boolean
);

create table tipo_entidade
(
    id            serial
        constraint pk_tipo_entidade primary key,
    tipo          varchar not null,
    criado_em     timestamp,
    atualizado_em timestamp,
    apagado       boolean
);

create table entidade
(
    id                  serial
        constraint pk_entidade
            primary key,
    nome                varchar not null,
    nome_fantasia       varchar not null,
    data_fundacao       date    not null,
    entidade_pai     integer
        constraint fk_entidade_entidade_pai_entidade
            references entidade,
    status              integer not null
        constraint fk_endidate_status_status
            references status,
    id_endereco         integer not null
        constraint fk_entidade_id_endereco_endereco
            references endereco,
    id_tipo_entidade integer not null
        constraint fk_entidade_id_tipo_ent_tipo_entidade references tipo_entidade,
    criado_em           timestamp,
    atualizado_em       timestamp,
    apagado             boolean
);

create table projeto
(
    id                  serial
        constraint pk_projeto
            primary key,
    sigla               varchar(12)   not null unique,
    titulo              varchar(150)  not null,
    valor_estimado      bigint        not null,
    objetivo            varchar(2000) not null,
    objetivo_especifico varchar(2000) not null,
    status              integer       not null
        constraint fk_projeto_status_status
            references status,
    id_entidade      integer       not null
        constraint fk_projeto_id_entidade_entidade references entidade,
    criado_em           timestamp,
    atualizado_em       timestamp,
    apagado             boolean
);

create table tipo_documento
(
    id            serial
        constraint pk_tipo_documento
            primary key,
    titulo        varchar not null,
    sigla         varchar not null,
    criado_em     timestamp,
    atualizado_em timestamp,
    apagado       boolean
);

create table documento
(
    id                integer not null
        constraint pk_documento
            primary key,
    id_entidade    integer not null
        constraint fk_documento_id_entidade_entidade
            references entidade,
    id_tipo_documento integer not null
        constraint fk_documento_id_tp_doc_tipo_documento
            references tipo_documento,
    numero            varchar not null,
    orgao_emissor     varchar,
    status            integer not null
        constraint fk_documento_status_status
            references status,
    criado_em         timestamp,
    atualizado_em     timestamp,
    apagado           boolean
);
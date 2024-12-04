create table prospeccao
(
    id                         serial primary key,
    id_cartaconsulta           integer      not null references cartaconsulta,
    id_organizacao_prospectora integer      not null references organizacao,
    id_pessoa_prospectora      integer      not null references pessoa,
    id_organizacao_prospectada integer      not null references organizacao,
    tipo_prospeccao            varchar(255) not null,
    status_prospeccao          varchar(255) not null,
    data_prospeccao            timestamp,
    criado_em                  timestamp    not null default current_timestamp,
    atualizado_em              timestamp,
    apagado                    bool         not null default false
);

create table prospeccao_interessado
(
    id               serial primary key,
    id_prospeccao    integer      not null references prospeccao,
    id_pessoa        integer      not null references pessoa,
    email_prospeccao varchar(255) not null,
    criado_em        timestamp    not null default current_timestamp,
    atualizado_em    timestamp,
    apagado          bool         not null default false
);
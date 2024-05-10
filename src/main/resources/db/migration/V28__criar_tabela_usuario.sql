create table usuario
(
    id           varchar
        constraint pk_usuario primary key,
    email        varchar not null unique,
    senha        varchar,
    id_pessoa    integer not null
        constraint fk_usuario_id_pessoa_pessoa references pessoa (id),
    sub_novo     varchar not null,
    access_token varchar not null
);

create table usuario_papeis
(
    usuario_id varchar not null
        constraint fk_usuario_papeis_id_usuario_usuario references usuario (id),
    papeis      varchar not null
)
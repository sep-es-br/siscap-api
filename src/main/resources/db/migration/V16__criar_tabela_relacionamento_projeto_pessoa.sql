create table projeto_pessoa
(
    id serial
        constraint pk_projeto_pessoa primary key,
    id_projeto     integer not null
        constraint fk_proj_pessoa_id_projeto_projeto references projeto(id),
    id_pessoa     integer not null
        constraint fk_proj_pessoa_id_pessoa_pessoa references pessoa(id)
)
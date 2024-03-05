create table pessoa
(
    id             serial
        constraint pk_pessoa primary key,
    nome           varchar not null,
    nome_social    varchar,
    nacionalidade  varchar,
    genero         varchar,
    cpf            varchar,
    id_endereco    integer
        constraint fk_pessoa_id_endereco_endereco references endereco,
    caminho_imagem varchar,
    criado_em      timestamp,
    atualizado_em  timestamp,
    apagado        boolean
);
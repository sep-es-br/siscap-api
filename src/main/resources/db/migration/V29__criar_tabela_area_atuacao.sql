create table area_atuacao
(
    id   varchar
        constraint pk_area_atuacao primary key,
    nome varchar not null unique
);

create table pessoa_area_atuacao
(
    id_area_atuacao varchar
        constraint fk_pes_ar_at_id_area_atuacao_area_atuacao references area_atuacao,
    id_pessoa       integer
        constraint fk_pes_ar_at_id_pessoa_pessoa references pessoa
);
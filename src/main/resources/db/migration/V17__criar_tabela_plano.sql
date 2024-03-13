create table plano
(
    id serial
        constraint pk_plano primary key,
    nome varchar not null,
    criado_em     timestamp,
    atualizado_em timestamp,
    apagado       boolean default false
);

insert into plano (id, nome, criado_em) values (1, 'Realiza+',  '2024-03-13 13:32:02.780759');

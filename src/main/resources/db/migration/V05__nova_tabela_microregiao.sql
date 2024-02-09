create table microregiao
(
    id            serial
        constraint pk_microregiao primary key,
    nome          varchar not null,
    criado_em     timestamp,
    atualizado_em timestamp,
    apagado       boolean
);

alter table projeto
    add column id_microregiao integer;
alter table projeto
    add constraint fk_projeto_id_microregiao_microregiao foreign key (id_microregiao) references microregiao (id);

insert into microregiao (id, nome, criado_em, atualizado_em, apagado)
values (1, 'Metropolitana', '2024-02-08 15:12:27.000000', null, false);

insert into microregiao (id, nome, criado_em, atualizado_em, apagado)
values (2, 'Central Serrana', '2024-02-08 15:12:27.000000', null, false);

insert into microregiao (id, nome, criado_em, atualizado_em, apagado)
values (3, 'Sudoeste Serrana', '2024-02-08 15:12:27.000000', null, false);

insert into microregiao (id, nome, criado_em, atualizado_em, apagado)
values (4, 'Litoral Sul', '2024-02-08 15:12:27.000000', null, false);

insert into microregiao (id, nome, criado_em, atualizado_em, apagado)
values (5, 'Central Sul', '2024-02-08 15:12:27.000000', null, false);

insert into microregiao (id, nome, criado_em, atualizado_em, apagado)
values (6, 'Caparaó', '2024-02-08 15:12:27.000000', null, false);

insert into microregiao (id, nome, criado_em, atualizado_em, apagado)
values (7, 'Rio Doce', '2024-02-08 15:12:27.000000', null, false);

insert into microregiao (id, nome, criado_em, atualizado_em, apagado)
values (8, 'Centro-Oeste', '2024-02-08 15:12:27.000000', null, false);

insert into microregiao (id, nome, criado_em, atualizado_em, apagado)
values (9, 'Nordeste', '2024-02-08 15:12:27.000000', null, false);

insert into microregiao (id, nome, criado_em, atualizado_em, apagado)
values (10, 'Noroeste', '2024-02-08 15:12:27.000000', null, false);

update projeto set id_microregiao = 1 where projeto.id_microregiao is null;
alter table projeto alter column id_microregiao set not null;

SELECT SETVAL((SELECT PG_GET_SERIAL_SEQUENCE('"microregiao"', 'id')), (SELECT (MAX("id") + 1) FROM "microregiao"), FALSE);
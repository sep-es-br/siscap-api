create table microrregiao
(
    id            serial
        constraint pk_microrregiao primary key,
    nome          varchar not null,
    criado_em     timestamp,
    atualizado_em timestamp,
    apagado       boolean
);

alter table projeto
    add column id_microrregiao integer;
alter table projeto
    add constraint fk_projeto_id_microrregiao_microrregiao foreign key (id_microrregiao) references microrregiao (id);

insert into microrregiao (id, nome, criado_em, atualizado_em, apagado)
values (1, 'Metropolitana', '2024-02-08 15:12:27.000000', null, false);

insert into microrregiao (id, nome, criado_em, atualizado_em, apagado)
values (2, 'Central Serrana', '2024-02-08 15:12:27.000000', null, false);

insert into microrregiao (id, nome, criado_em, atualizado_em, apagado)
values (3, 'Sudoeste Serrana', '2024-02-08 15:12:27.000000', null, false);

insert into microrregiao (id, nome, criado_em, atualizado_em, apagado)
values (4, 'Litoral Sul', '2024-02-08 15:12:27.000000', null, false);

insert into microrregiao (id, nome, criado_em, atualizado_em, apagado)
values (5, 'Central Sul', '2024-02-08 15:12:27.000000', null, false);

insert into microrregiao (id, nome, criado_em, atualizado_em, apagado)
values (6, 'Caparaó', '2024-02-08 15:12:27.000000', null, false);

insert into microrregiao (id, nome, criado_em, atualizado_em, apagado)
values (7, 'Rio Doce', '2024-02-08 15:12:27.000000', null, false);

insert into microrregiao (id, nome, criado_em, atualizado_em, apagado)
values (8, 'Centro-Oeste', '2024-02-08 15:12:27.000000', null, false);

insert into microrregiao (id, nome, criado_em, atualizado_em, apagado)
values (9, 'Nordeste', '2024-02-08 15:12:27.000000', null, false);

insert into microrregiao (id, nome, criado_em, atualizado_em, apagado)
values (10, 'Noroeste', '2024-02-08 15:12:27.000000', null, false);

update projeto set id_microrregiao = 1 where projeto.id_microrregiao is null;
alter table projeto alter column id_microrregiao set not null;

SELECT SETVAL((SELECT PG_GET_SERIAL_SEQUENCE('"microrregiao"', 'id')), (SELECT (MAX("id") + 1) FROM "microrregiao"), FALSE);
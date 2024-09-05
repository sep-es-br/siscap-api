create table valor
(
    id            integer primary key,
    tipo          varchar(255) not null,
    data_inicio   timestamp,
    data_fim      timestamp,
    criado_em     timestamp,
    atualizado_em timestamp,
    apagado       boolean
);

insert into valor (id, tipo, data_inicio, data_fim, criado_em, atualizado_em, apagado)
values (1, 'Estimado', current_timestamp, null, current_timestamp, null, false);

insert into valor (id, tipo, data_inicio, data_fim, criado_em, atualizado_em, apagado)
values (2, 'Em captação', current_timestamp, null, current_timestamp, null, false);

insert into valor (id, tipo, data_inicio, data_fim, criado_em, atualizado_em, apagado)
values (3, 'Captado', current_timestamp, null, current_timestamp, null, false);

insert into valor (id, tipo, data_inicio, data_fim, criado_em, atualizado_em, apagado)
values (4, 'Contratado', current_timestamp, null, current_timestamp, null, false);

insert into valor (id, tipo, data_inicio, data_fim, criado_em, atualizado_em, apagado)
values (5, 'Contra Partida', current_timestamp, null, current_timestamp, null, false);

insert into valor (id, tipo, data_inicio, data_fim, criado_em, atualizado_em, apagado)
values (6, 'Orçado', current_timestamp, null, current_timestamp, null, false);

insert into valor (id, tipo, data_inicio, data_fim, criado_em, atualizado_em, apagado)
values (7, 'Empenhado', current_timestamp, null, current_timestamp, null, false);

insert into valor (id, tipo, data_inicio, data_fim, criado_em, atualizado_em, apagado)
values (8, 'Reservado', current_timestamp, null, current_timestamp, null, false);

insert into valor (id, tipo, data_inicio, data_fim, criado_em, atualizado_em, apagado)
values (9, 'Liquidado', current_timestamp, null, current_timestamp, null, false);

insert into valor (id, tipo, data_inicio, data_fim, criado_em, atualizado_em, apagado)
values (10, 'Pago', current_timestamp, null, current_timestamp, null, false);
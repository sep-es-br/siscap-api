create table papel (
    id serial primary key,
    tipo varchar(255) not null,
    criado_em timestamp,
    atualizado_em timestamp,
    apagado boolean
);

create table equipe (
    id serial primary key,
    tipo varchar(255) not null,
    criado_em timestamp,
    atualizado_em timestamp,
    apagado boolean
);

insert into papel (id, tipo, criado_em, atualizado_em, apagado) values (1, 'Gerente de Projeto', current_timestamp, null, false);
insert into papel (id, tipo, criado_em, atualizado_em, apagado) values (2, 'Responsável Proponente', current_timestamp, null, false);
insert into papel (id, tipo, criado_em, atualizado_em, apagado) values (3, 'Proponente', current_timestamp, null, false);
insert into papel (id, tipo, criado_em, atualizado_em, apagado) values (4, 'Patrocinador', current_timestamp, null, false);
insert into papel (id, tipo, criado_em, atualizado_em, apagado) values (5, 'Membro do Projeto', current_timestamp, null, false);

insert into equipe (id, tipo, criado_em, atualizado_em, apagado) values (1, 'Elaboração', current_timestamp, null, false);
insert into equipe (id, tipo, criado_em, atualizado_em, apagado) values (2, 'Execução', current_timestamp, null, false);
insert into equipe (id, tipo, criado_em, atualizado_em, apagado) values (3, 'Captação', current_timestamp, null, false);
insert into equipe (id, tipo, criado_em, atualizado_em, apagado) values (4, 'Monitoramento', current_timestamp, null, false);
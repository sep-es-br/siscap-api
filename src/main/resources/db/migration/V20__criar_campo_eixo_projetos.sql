create table eixo
(
    id serial
        constraint pk_eixo primary key,
    nome varchar not null,
    id_plano integer not null
        constraint fk_eixo_id_plano_plano references plano,
    criado_em     timestamp,
    atualizado_em timestamp,
    apagado       boolean default false
);

alter table projeto add column id_eixo integer;
alter table projeto
    add constraint fk_projeto_id_eixo_eixo foreign key (id_eixo) references eixo (id);

insert into eixo (id, nome, id_plano, criado_em) values (1, 'Eixo 1: +Qualidade de vida', 1, '2024-03-13 13:32:02.780759');
insert into eixo (id, nome, id_plano, criado_em) values (2, 'Eixo 2: +Des. com sustentabilidade', 1, '2024-03-13 13:32:02.780759');
insert into eixo (id, nome, id_plano, criado_em) values (3, 'Eixo 3: +Resultados', 1, '2024-03-13 13:32:02.780759');

update projeto set id_plano = 1 where id_plano is null;
update projeto set id_eixo = 1 where id_eixo is null;

alter table projeto alter column id_plano set not null;

alter table projeto alter column id_eixo set not null;

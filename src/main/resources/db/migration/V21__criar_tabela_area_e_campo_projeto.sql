create table area
(
    id serial
        constraint pk_area primary key,
    nome varchar not null,
    id_eixo integer not null
        constraint fk_area_id_eixo_eixo references eixo,
    criado_em     timestamp,
    atualizado_em timestamp,
    apagado       boolean default false
);

insert into area (id, nome, id_eixo, criado_em) values (1, 'Educação, Cultura, Esporte e Lazer', 1, '2024-03-13 13:32:02.780759');
insert into area (id, nome, id_eixo, criado_em) values (2, 'Segurança Públic e Justiça', 1, '2024-03-13 13:32:02.780759');
insert into area (id, nome, id_eixo, criado_em) values (3, 'Prot.Social, Saúde e Dir.Humanos', 1, '2024-03-13 13:32:02.780759');
insert into area (id, nome, id_eixo, criado_em) values (4, 'Agricultura e Meio Ambiente', 2, '2024-03-13 13:32:02.780759');
insert into area (id, nome, id_eixo, criado_em) values (5, 'D.Econ, C, T & Inovação, Turismo', 2, '2024-03-13 13:32:02.780759');
insert into area (id, nome, id_eixo, criado_em) values (6, 'Infraestrutura', 2, '2024-03-13 13:32:02.780759');
insert into area (id, nome, id_eixo, criado_em) values (7, 'Gestão Públic Inovadora', 3, '2024-03-13 13:32:02.780759');

alter table projeto add column id_area integer;
alter table projeto
    add constraint fk_projeto_id_area_area foreign key (id_area) references area (id);

update projeto set id_area = 1 where id_area is null;

alter table projeto alter column id_area set not null;

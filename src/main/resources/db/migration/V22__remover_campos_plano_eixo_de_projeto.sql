alter table projeto drop column id_eixo;
alter table projeto drop column id_plano;

alter table projeto alter column id_area drop not null;
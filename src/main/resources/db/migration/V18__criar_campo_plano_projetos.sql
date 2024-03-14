alter table projeto add column id_plano integer;
alter table projeto
    add constraint fk_projeto_id_plano_plano foreign key (id_plano) references plano (id);
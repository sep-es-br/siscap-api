alter table pessoa
    add column id_organizacao integer;
alter table pessoa
    add constraint fk_pessoa_id_organizacao_organizacao foreign key (id_organizacao) references organizacao (id);
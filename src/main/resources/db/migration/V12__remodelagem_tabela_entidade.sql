alter table entidade rename column nome_fantasia to abreviatura;

alter table entidade drop constraint if exists fk_entidade_id_endereco_endereco;
alter table entidade drop column if exists id_endereco;

alter table entidade add column id_cidade integer;
alter table entidade add constraint fk_entidade_id_cidade_cidade foreign key (id_cidade) references cidade(id);

alter table entidade add column telefone varchar;
alter table entidade add column fax varchar;
alter table entidade add column email varchar;
alter table entidade add column site varchar;

alter table entidade add column id_pessoa integer;
alter table entidade add constraint fk_entidade_id_pessoa_pessoa foreign key (id_pessoa) references pessoa(id);

alter table entidade drop column if exists data_fundacao;

alter table entidade add column nome_imagem varchar;

drop table if exists documento;
drop table if exists tipo_documento;

alter table entidade add column cnpj varchar unique;
alter table pessoa add unique (cpf);

alter table entidade add column id_pais integer;
alter table entidade add constraint fk_entidade_id_pais_pais foreign key (id_pais) references pais(id);

alter table endereco alter column numero type varchar;
alter table cidade alter column id_estado drop not null;
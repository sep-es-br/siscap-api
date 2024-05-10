alter table entidade
    rename to organizacao;
alter table organizacao
    rename constraint pk_entidade to pk_organizacao;
alter table organizacao
    rename constraint fk_entidade_entidade_pai_entidade to fk_organizacao_organizacao_pai_organizacao;
alter table organizacao
    rename constraint fk_endidate_status_status to fk_organizacao_status_status;
alter table organizacao
    rename constraint fk_entidade_id_tipo_ent_tipo_entidade to fk_organizacao_id_tipo_org_tipo_entidade;
alter table organizacao
    rename constraint fk_entidade_id_cidade_cidade to fk_organizacao_id_cidade_cidade;
alter table organizacao
    rename constraint fk_entidade_id_pessoa_pessoa to fk_organizacao_id_pessoa_pessoa;
alter table organizacao
    rename constraint fk_entidade_id_pais_pais to fk_organizacao_id_pais_pais;
alter table organizacao
    rename column entidade_pai to organizacao_pai;
alter table organizacao
    rename column id_tipo_entidade to id_tipo_organizacao;
alter table projeto
    rename column id_entidade to id_organizacao;

alter table organizacao
    add column id_estado integer;
alter table organizacao
    add constraint fk_organizacao_id_estado_estado foreign key (id_estado) references estado (id);

alter table organizacao
    rename column abreviatura to nome_fantasia;
alter table organizacao
    drop column if exists fax;


alter table tipo_entidade
    rename to tipo_organizacao;
alter table tipo_organizacao
    rename constraint pk_tipo_entidade to pk_tipo_organizacao;

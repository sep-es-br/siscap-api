
alter table pessoa_area_atuacao drop constraint fk_pes_ar_at_id_area_atuacao_area_atuacao;

-- Remapeamento da PK da tabela area_atuacao de varchar para serial
alter table area_atuacao add column id_temp serial;
alter table area_atuacao drop column id cascade;
alter table area_atuacao rename column id_temp to id;
alter table area_atuacao add primary key (id);

-- Remapeamento da PK da tabela pessoa_area_atuacao de varchar para serial
alter table pessoa_area_atuacao add column id_temp serial;
alter table pessoa_area_atuacao drop column id_area_atuacao;
alter table pessoa_area_atuacao rename column id_temp to id_area_atuacao;

alter table pessoa_area_atuacao add constraint fk_pes_ar_at_id_area_atuacao_area_atuacao foreign key (id_area_atuacao) references area_atuacao(id);


alter table cidade
    add column id_microrregiao integer;
alter table cidade
    add constraint fk_cidade_id_microrregiao_microrregiao foreign key (id_microrregiao) references microrregiao (id);

update cidade set id_microrregiao = 1 where cidade.id_microrregiao is null;
alter table cidade alter column id_microrregiao set not null;
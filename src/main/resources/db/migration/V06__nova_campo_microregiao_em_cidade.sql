alter table cidade
    add column id_microregiao integer;
alter table cidade
    add constraint fk_cidade_id_microregiao_microregiao foreign key (id_microregiao) references microregiao (id);

update cidade set id_microregiao = 1 where cidade.id_microregiao is null;
alter table cidade alter column id_microregiao set not null;
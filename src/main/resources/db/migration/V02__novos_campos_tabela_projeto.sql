alter table projeto add column situacao_problema varchar(2000);
update projeto set situacao_problema = '.' where projeto.situacao_problema is null;
alter table projeto alter column situacao_problema set not null;

alter table projeto add column solucoes_propostas varchar(2000);
update projeto set solucoes_propostas = '.' where projeto.solucoes_propostas is null;
alter table projeto alter column solucoes_propostas set not null;

alter table projeto add column impactos varchar(2000);
update projeto set impactos = '.' where projeto.impactos is null;
alter table projeto alter column impactos set not null;

alter table projeto add column arranjos_instituicionais varchar(2000);
update projeto set arranjos_instituicionais = '.' where projeto.arranjos_instituicionais is null;
alter table projeto alter column arranjos_instituicionais set not null;
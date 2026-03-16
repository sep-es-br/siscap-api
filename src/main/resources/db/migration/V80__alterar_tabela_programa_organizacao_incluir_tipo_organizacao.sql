-- 04/03/2026
-- cria uma nova coluna na tabela programa_organizacao
-- para armazenar o codigo do tipo de organizacao do programa
-- que podera ser 1-Gestor e 2-Executor
-- para deixar claro essas possicoes no programa
alter table programa_organizacao
    add column tipo_organizacao integer;

alter table programa_organizacao
    add constraint chk_programa_org_tipo
    check (tipo_organizacao in (1, 2));
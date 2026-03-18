-- 10/03/2026
-- cria uma nova coluna na tabela programa
-- para armazenar o status em que o programa se encontra
-- que podera ser 1 - Edição  2 - Aguardando Assinaturas  3 - Assinado  4 - Autuado  5 - Recusado
alter table programa
    add column status integer;

alter table programa
    add constraint chk_programa_status
    check (status in (1, 2, 3, 4, 5));
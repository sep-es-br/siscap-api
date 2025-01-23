-- 23/01/2024
-- Adiciona coluna "prospectado" na tabela "carta_consulta"
-- Propósito de evitar alteração/exclusão da entidade carta consulta uma vez
-- que uma prospecção na qual a carta consulta está vinculada tenha sido
-- prospectada (e-mail enviado para organizações prospectadas).

alter table cartaconsulta
    add column prospectado boolean not null default false;
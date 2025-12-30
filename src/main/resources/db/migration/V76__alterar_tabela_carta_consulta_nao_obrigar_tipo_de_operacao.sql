-- 29/12/2025
-- campo operação nao deve ser mais obrigatório
-- 

alter table cartaconsulta
    alter column id_operacao DROP NOT NULL;
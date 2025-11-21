-- 18/11/2025
-- Criacao novas colunas no programa armazenar o percentual do custo adm 
-- a ser aplicado ao valor total 
alter table programa
    add column percentual_custo_administrativo numeric(5, 2),
    add column valor_calculado_total numeric(25, 2)
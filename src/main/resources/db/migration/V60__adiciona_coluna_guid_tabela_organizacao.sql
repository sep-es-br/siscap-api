-- 31/01/2025
-- Adiciona coluna "guid" na tabela "organizacao"
-- Propósito é vincular dados de Organização do banco da aplicação com
-- as APIs Organograma e Acesso Cidadão, á fim de manter sincronismo de dados

alter table organizacao
    add column guid varchar(255) unique;
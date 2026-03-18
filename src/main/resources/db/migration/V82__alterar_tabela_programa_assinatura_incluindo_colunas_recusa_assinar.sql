-- 11/03/2026
-- cria uma novas colunas na tabela programa_assinatura_documento_edocs
-- para armazenar a data de recusa e a justificativa da recusa do 
-- assinante não assinar o programa;
alter table programa_assinatura_documento_edocs
    add column data_recusa timestamp,
    add column justificativa_recusa varchar(500);

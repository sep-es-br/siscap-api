-- 29/01/2026
-- cria uma nova coluna na tabela projeto_parecer
-- para armazenar o codigo do registro do arquivo entranhado
-- no E-Docs para geração do link de acesso ao mesmo via 
-- relatorio do programa e outros
alter table projeto_parecer
    add column registro_arquivo_edocs VARCHAR(50);
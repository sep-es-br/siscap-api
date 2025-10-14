
-- 10/10/2025
-- Criacao de campo para armazenar descricao de uma justificativa para
-- exclusao do DIC - que sera logica dependendo do status do DIC 

-- Adição da coluna 
ALTER TABLE public.projeto
    ADD COLUMN justificativa_exclusao_logica VARCHAR(500);


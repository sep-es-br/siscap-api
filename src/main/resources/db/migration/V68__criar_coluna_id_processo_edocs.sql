
-- 15/09/2025
-- Criacao de campo para armazenar o id do processo criado no E-Docs
-- na autuacao inicial do DIC

-- Adição da coluna 
ALTER TABLE public.projeto
    ADD COLUMN id_processo_edocs VARCHAR(50);


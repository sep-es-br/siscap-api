
-- 02/09/2025
-- Criacao de campo para armazenar o id do documento capturado e entranahdo ao E-Docs
-- na autuacao do DIC

-- Adição da coluna 
ALTER TABLE public.projeto
    ADD COLUMN id_documento_edocs VARCHAR(50);


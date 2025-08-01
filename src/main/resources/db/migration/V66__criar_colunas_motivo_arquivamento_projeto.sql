
-- 01/08/2025
-- Criacao de campos para gravar o codigo do motivo de arquivamento
-- e a justificativa desse arquivamento
-- 

-- Adição da coluna 
ALTER TABLE public.projeto
    ADD COLUMN justificativa_arquivamento VARCHAR(255);

-- adição da coluna que vai mapear em uma lista de motivos pre-definidos o id do motivo selecionado pelo
-- usuario que arquivou o projeto.
ALTER TABLE public.projeto
    ADD COLUMN id_tipo_motivo_arquivamento integer;

ALTER TABLE public.projeto
    ADD CONSTRAINT fk_projeto_id_tipo_motivo_arquivamento
        FOREIGN KEY (id_tipo_motivo_arquivamento)
        REFERENCES public.tipo_motivo_arquivamento (id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION;


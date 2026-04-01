-- 31/03/2026
-- cria uma nova coluna na tabela projeto
-- para armazenar o sub do redator do DIC;
alter table projeto
    add column id_pessoa_redator integer,
    add CONSTRAINT fk_projeto_id_pessoa_redator FOREIGN KEY (id_pessoa_redator)
        REFERENCES public.pessoa (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION;

-- 31/03/2026
-- cria uma nova coluna na tabela projeto
-- para armazenar o sub do redator do DIC;
DO $$
BEGIN
    -- coluna
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'projeto'
          AND column_name = 'id_pessoa_redator'
    ) THEN
        ALTER TABLE projeto
        ADD COLUMN id_pessoa_redator INTEGER;
    END IF;

    -- constraint
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_projeto_id_pessoa_redator'
    ) THEN
        ALTER TABLE projeto
        ADD CONSTRAINT fk_projeto_id_pessoa_redator
        FOREIGN KEY (id_pessoa_redator)
        REFERENCES public.pessoa (id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION;
    END IF;
END $$;

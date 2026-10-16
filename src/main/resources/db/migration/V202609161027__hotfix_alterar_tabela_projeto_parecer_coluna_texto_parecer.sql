-- Altera o tipo da coluna texto_parecer de VARCHAR(20000) para TEXT,
-- permitindo armazenar o conteúdo HTML gerado pelo editor de texto rico,
-- cujas tags de formatação podem ultrapassar o limite original da coluna.

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'projeto_parecer'
          AND column_name = 'texto_parecer'
          AND data_type = 'character varying'
    ) THEN

        ALTER TABLE public.projeto_parecer
            ALTER COLUMN texto_parecer TYPE TEXT;

    END IF;
END $$;
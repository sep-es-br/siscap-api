ALTER TABLE projeto_parecer
    ALTER COLUMN texto_parecer DROP NOT NULL;

ALTER TABLE projeto_parecer
ADD CONSTRAINT chk_projeto_parecer_texto_ou_arquivo
CHECK (
    COALESCE(TRIM(texto_parecer), '') <> ''
    OR (
        COALESCE(TRIM(nome_arquivo), '') <> ''
        AND COALESCE(TRIM(nome_original_arquivo), '') <> ''
    )
);
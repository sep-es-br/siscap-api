ALTER TABLE projeto_parecer
DROP CONSTRAINT IF EXISTS chk_projeto_parecer_texto_ou_arquivo;

ALTER TABLE projeto_parecer
ADD CONSTRAINT chk_projeto_parecer_texto_ou_arquivo_enviado
CHECK (
    status_parecer NOT IN (2, 3, 4)
    OR (
        COALESCE(TRIM(texto_parecer), '') <> ''
        OR (
            COALESCE(TRIM(nome_arquivo), '') <> ''
            AND COALESCE(TRIM(nome_original_arquivo), '') <> ''
        )
    )
);
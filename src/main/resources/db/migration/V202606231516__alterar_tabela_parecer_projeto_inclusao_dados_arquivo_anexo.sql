ALTER TABLE projeto_parecer
    ADD COLUMN IF NOT EXISTS nome_arquivo VARCHAR(255);

ALTER TABLE projeto_parecer
    ADD COLUMN IF NOT EXISTS nome_original_arquivo VARCHAR(255);
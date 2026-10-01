ALTER TABLE projeto
    ALTER COLUMN objetivo DROP NOT NULL,
    ALTER COLUMN objetivo_especifico DROP NOT NULL,
    ALTER COLUMN situacao_problema DROP NOT NULL,
    ALTER COLUMN solucoes_propostas DROP NOT NULL,
    ALTER COLUMN impactos DROP NOT NULL,
    ALTER COLUMN arranjos_institucionais DROP NOT NULL;


ALTER TABLE projeto
    ADD CONSTRAINT ck_projeto_sigla_obrigatoria_ativo
    CHECK (
        apagado = true
        OR NULLIF(TRIM(sigla), '') IS NOT NULL
    );
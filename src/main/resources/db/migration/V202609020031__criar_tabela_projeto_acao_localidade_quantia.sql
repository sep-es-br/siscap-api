-- ==========================================================================
-- Rateio por localidade das ações do projeto (rateio regioes acao projeto)
-- ==========================================================================
CREATE SEQUENCE projeto_acao_localidade_quantia_id_seq START
WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE projeto_acao_localidade_quantia (
    id BIGINT NOT NULL,
    id_projeto_acao INTEGER NOT NULL,
    id_localidade BIGINT NOT NULL,
    percentual NUMERIC(7, 4) NOT NULL,
    quantia NUMERIC(25, 2) NOT NULL,
    apagado BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_projeto_acao_localidade_quantia PRIMARY KEY (id),
    CONSTRAINT fk_projeto_acao_localidade_quantia_acao FOREIGN KEY (id_projeto_acao) REFERENCES projeto_acao (id),
    CONSTRAINT fk_projeto_acao_localidade_quantia_localidade FOREIGN KEY (id_localidade) REFERENCES localidade (id),
    CONSTRAINT ck_projeto_acao_localidade_quantia_percentual CHECK ( percentual >= 0 AND percentual <= 100 ),
    CONSTRAINT ck_projeto_acao_localidade_quantia_quantia CHECK (quantia >= 0)
);

ALTER SEQUENCE projeto_acao_localidade_quantia_id_seq OWNED BY projeto_acao_localidade_quantia.id;

ALTER TABLE projeto_acao_localidade_quantia
ALTER COLUMN id
SET DEFAULT nextval (
    'projeto_acao_localidade_quantia_id_seq'
);

-- Uma mesma localidade não pode aparecer mais de uma vez
-- ATIVA dentro da mesma ação.
--
-- Como existe soft delete, não usamos UNIQUE convencional.
CREATE UNIQUE INDEX uk_projeto_acao_localidade_quantia_ativo ON projeto_acao_localidade_quantia (
    id_projeto_acao,
    id_localidade
)
WHERE
    apagado = FALSE;

-- Índices auxiliares para consultas/joins
CREATE INDEX idx_projeto_acao_localidade_quantia_acao ON projeto_acao_localidade_quantia (id_projeto_acao);

CREATE INDEX idx_projeto_acao_localidade_quantia_localidade ON projeto_acao_localidade_quantia (id_localidade);
-- =========================================
-- TABELA: indicador_gestao_externo
-- =========================================
CREATE TABLE indicador_gestao_externo (
    id BIGINT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    ativa BOOLEAN NOT NULL,
    model_label VARCHAR(1000),

    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP,
    apagado BOOLEAN NOT NULL DEFAULT FALSE
);

-- =========================================
-- TABELA: indicador_label
-- =========================================
CREATE TABLE indicador_label (
    id_label SERIAL PRIMARY KEY,
    nome VARCHAR(255),

    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP,
    apagado BOOLEAN NOT NULL DEFAULT FALSE
);

-- =========================================
-- TABELA: indicador_label_valor
-- =========================================
CREATE TABLE indicador_label_valor (
    id_label_valor SERIAL PRIMARY KEY,
    id_label INTEGER NOT NULL,
    valor VARCHAR(255),

    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP,
    apagado BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_label_valor_label
        FOREIGN KEY (id_label)
        REFERENCES indicador_label (id_label),

    CONSTRAINT uk_label_valor UNIQUE (id_label, valor)
);

-- =========================================
-- TABELA: indicador_gestao_label
-- =========================================
CREATE TABLE indicador_gestao_label (
    id_gestao BIGINT NOT NULL,
    id_label INTEGER NOT NULL,
    ordem INTEGER,

    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP,
    apagado BOOLEAN NOT NULL DEFAULT FALSE,

    PRIMARY KEY (id_gestao, id_label),

    CONSTRAINT fk_gestao_label_gestao
        FOREIGN KEY (id_gestao)
        REFERENCES indicador_gestao_externo (id),

    CONSTRAINT fk_gestao_label_label
        FOREIGN KEY (id_label)
        REFERENCES indicador_label (id_label)
);

-- =========================================
-- TABELA: indicador_organizador_externo
-- =========================================
CREATE TABLE indicador_organizador_externo (
    id INTEGER PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    model_name VARCHAR(255),
    id_organizador_pai INTEGER,

    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP,
    apagado BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_organizador_pai
        FOREIGN KEY (id_organizador_pai)
        REFERENCES indicador_organizador_externo (id)
);

-- =========================================
-- TABELA: indicador_externo
-- =========================================
CREATE TABLE indicador_externo (
    id_indicador INTEGER PRIMARY KEY,
    nome VARCHAR(255),
    unidade_medida VARCHAR(255),
    polaridade VARCHAR(255),
    medido_por VARCHAR(255),

    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP,
    apagado BOOLEAN NOT NULL DEFAULT FALSE
);

-- =========================================
-- TABELA: indicador_desafio_externo
-- =========================================
CREATE TABLE indicador_desafio_externo (
    id_desafio INTEGER PRIMARY KEY,
    nome VARCHAR(255),
    id_gestao BIGINT,

    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP,
    apagado BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_desafio_gestao
        FOREIGN KEY (id_gestao)
        REFERENCES indicador_gestao_externo (id)
);

-- =========================================
-- TABELA: indicador_fato_externo
-- =========================================
CREATE TABLE indicador_fato_externo (
    id_fato BIGSERIAL PRIMARY KEY,

    id_gestao BIGINT,
    id_desafio INTEGER,
    id_organizador INTEGER,
    id_indicador INTEGER,

    ano INTEGER,
    valor_meta NUMERIC(19,4),
    maior_ano_indicador INTEGER,
    maior_meta_indicador NUMERIC(19,4),

    dt_importacao TIMESTAMP NOT NULL,

    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP,
    apagado BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_fato_gestao
        FOREIGN KEY (id_gestao)
        REFERENCES indicador_gestao_externo (id),

    CONSTRAINT fk_fato_desafio
        FOREIGN KEY (id_desafio)
        REFERENCES indicador_desafio_externo (id_desafio),

    CONSTRAINT fk_fato_organizador
        FOREIGN KEY (id_organizador)
        REFERENCES indicador_organizador_externo (id),

    CONSTRAINT fk_fato_indicador
        FOREIGN KEY (id_indicador)
        REFERENCES indicador_externo (id_indicador)
);

-- =========================================
-- TABELA: organizador_label_valor
-- =========================================
CREATE TABLE organizador_label_valor (
    id_organizador INTEGER NOT NULL,
    id_label_valor INTEGER NOT NULL,

    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP,
    apagado BOOLEAN NOT NULL DEFAULT FALSE,

    PRIMARY KEY (id_organizador, id_label_valor),

    CONSTRAINT fk_org_label_valor_org
        FOREIGN KEY (id_organizador)
        REFERENCES indicador_organizador_externo (id),

    CONSTRAINT fk_org_label_valor_valor
        FOREIGN KEY (id_label_valor)
        REFERENCES indicador_label_valor (id_label_valor)
);
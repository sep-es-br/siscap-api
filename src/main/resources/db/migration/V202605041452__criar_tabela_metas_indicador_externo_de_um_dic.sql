-- ============================================
-- SEQUENCE
-- ============================================
CREATE SEQUENCE projeto_indicador_externo_meta_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- ============================================
-- TABELA: projeto_indicador_externo_meta
-- ============================================
CREATE TABLE projeto_indicador_externo_meta (
    
    id INTEGER NOT NULL DEFAULT nextval('projeto_indicador_externo_meta_id_seq'),
    
    idFato INTEGER NOT NULL,
    anoMeta INTEGER NOT NULL,
    valorMeta VARCHAR(255) NOT NULL,
    id_projeto_indicador INTEGER NOT NULL,
    apagado_em timestamp NULL,
	criado_em timestamp NOT NULL,

    CONSTRAINT pk_projeto_indicador_externo_meta PRIMARY KEY (id),

    CONSTRAINT fk_projeto_indicador_externo_meta
        FOREIGN KEY (id_projeto_indicador)
        REFERENCES projeto_indicador (id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION

);
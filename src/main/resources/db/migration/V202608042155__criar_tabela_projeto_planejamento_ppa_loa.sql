-- VXXX__criar_tabela_projeto_planejamento_ppa_loa.sql

CREATE SEQUENCE projeto_planejamento_ppa_loa_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


CREATE TABLE projeto_planejamento_ppa_loa
(
    id              INTEGER NOT NULL  DEFAULT nextval('projeto_planejamento_ppa_loa_id_seq'),

    id_projeto      INTEGER NOT NULL,
    cod_acao        VARCHAR(255),
    cod_funcao      VARCHAR(255),
    cod_programa    VARCHAR(255),
    ano             VARCHAR(255),
    cod_uo          VARCHAR(255),

    criado_em       TIMESTAMP NOT NULL DEFAULT now(),
    atualizado_em   TIMESTAMP,
    apagado         BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT pk_projeto_planejamento_ppa_loa
        PRIMARY KEY (id),

    CONSTRAINT fk_projeto_planejamento_ppa_loa_projeto
        FOREIGN KEY (id_projeto)
        REFERENCES projeto (id)
        
);


ALTER SEQUENCE projeto_planejamento_ppa_loa_id_seq
    OWNED BY projeto_planejamento_ppa_loa.id;


CREATE INDEX idx_projeto_planejamento_ppa_loa_id_projeto
    ON projeto_planejamento_ppa_loa (id_projeto);
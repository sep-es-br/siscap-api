CREATE SEQUENCE projeto_ods_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE projeto_ods (
    
    id INTEGER NOT NULL DEFAULT nextval('projeto_ods_id_seq'),

    id_projeto INTEGER NOT NULL,

    id_ods INTEGER NOT NULL,

    criado_em TIMESTAMP NOT NULL DEFAULT now(),

    atualizado_em TIMESTAMP,

    apagado BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT pk_projeto_ods
        PRIMARY KEY (id),

    CONSTRAINT fk_projeto_ods_projeto
        FOREIGN KEY (id_projeto)
        REFERENCES projeto(id),

    CONSTRAINT uk_projeto_ods
        UNIQUE (id_projeto, id_ods)
);

CREATE INDEX idx_projeto_ods_projeto
    ON projeto_ods(id_projeto);

CREATE INDEX idx_projeto_ods_ods
    ON projeto_ods(id_ods);
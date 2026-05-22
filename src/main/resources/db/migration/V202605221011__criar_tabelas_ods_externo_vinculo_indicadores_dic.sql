
CREATE SEQUENCE ods_externo_id_seq
START WITH 1
INCREMENT BY 1;

CREATE TABLE ods_externo (
    id INTEGER NOT NULL DEFAULT nextval('ods_externo_id_seq'),
    ods_id INTEGER NOT NULL,
    ods_ordem INTEGER NOT NULL,
    ods_descricao VARCHAR(2000) NOT NULL,
    ods_nome VARCHAR(2000) NOT NULL,

	criado_em timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em timestamp without time zone,
    apagado boolean NOT NULL DEFAULT false,
	
    CONSTRAINT pk_ods_externo PRIMARY KEY (id),
    CONSTRAINT uk_ods_externo_ods_id UNIQUE (ods_id)
);

CREATE SEQUENCE ods_indicador_externo_id_seq
START WITH 1
INCREMENT BY 1;

CREATE TABLE ods_indicador_externo (
    id INTEGER NOT NULL DEFAULT nextval('ods_indicador_externo_id_seq'),
    id_indicador_externo INTEGER NOT NULL,
    id_ods_externo INTEGER NOT NULL,

    criado_em TIMESTAMP NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMP NULL,
    apagado BOOLEAN NOT NULL DEFAULT false,

    CONSTRAINT pk_ods_indicador_externo
        PRIMARY KEY (id),

    CONSTRAINT fk_ods_indicador_externo_indicador
        FOREIGN KEY (id_indicador_externo)
        REFERENCES indicador_externo(id_indicador),

    CONSTRAINT fk_ods_indicador_externo_ods
        FOREIGN KEY (id_ods_externo)
        REFERENCES ods_externo(id),

    CONSTRAINT uk_ods_indicador_externo
        UNIQUE (id_indicador_externo, id_ods_externo)
);

CREATE SEQUENCE projeto_indicador_ods_id_seq
START WITH 1
INCREMENT BY 1;

CREATE TABLE projeto_indicador_ods (
    id INTEGER NOT NULL DEFAULT nextval('projeto_indicador_ods_id_seq'),

    id_projeto_indicador INTEGER NOT NULL,
    id_ods_indicador_externo INTEGER NOT NULL,

    criado_em TIMESTAMP NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMP NULL,
    apagado BOOLEAN NOT NULL DEFAULT false,

    CONSTRAINT pk_projeto_indicador_ods
        PRIMARY KEY (id),

    CONSTRAINT fk_projeto_indicador_ods_projeto_indicador
        FOREIGN KEY (id_projeto_indicador)
        REFERENCES projeto_indicador(id),

    CONSTRAINT fk_projeto_indicador_ods_ods_indicador_externo
        FOREIGN KEY (id_ods_indicador_externo)
        REFERENCES ods_indicador_externo(id),

    CONSTRAINT uk_projeto_indicador_ods
        UNIQUE (id_projeto_indicador, id_ods_indicador_externo)		
);
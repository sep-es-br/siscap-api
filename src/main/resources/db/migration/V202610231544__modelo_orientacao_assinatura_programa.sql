CREATE SEQUENCE modelo_orientacao_assinatura_programa_id_seq START
    WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;


CREATE TABLE modelo_orientacao_assinatura_programa (

    id integer NOT NULL DEFAULT nextval('modelo_orientacao_assinatura_programa_id_seq'),
    guid_lotacao varchar(50) COLLATE pg_catalog."default" NOT NULL,
    titulo varchar(150) NOT NULL,
    versao  integer NOT NULL,
    conteudo text NOT NULL,
    ativo boolean NOT NULL DEFAULT TRUE,

    criado_em   TIMESTAMP NOT NULL DEFAULT now(),
    atualizado_em   TIMESTAMP,
    apagado BOOLEAN NOT NULL DEFAULT FALSE,
    
    CONSTRAINT pk_modelo_orientacao_assinatura_programa PRIMARY KEY (id),
    CONSTRAINT uk_modelo_orientacao_assinatura_programa UNIQUE (guid_lotacao, versao),
    CONSTRAINT ck_modelo_orientacao_assinatura_programa CHECK (versao > 0)

);

ALTER SEQUENCE modelo_orientacao_assinatura_programa_id_seq OWNED BY modelo_orientacao_assinatura_programa.id;

CREATE UNIQUE INDEX uk_modelo_orientacao_ativa_lotacao ON modelo_orientacao_assinatura_programa (guid_lotacao)
    WHERE ativo = TRUE AND apagado = FALSE;
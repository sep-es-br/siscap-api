
-- 02/06/2026
-- Atualizar lista de tipo de papeis criacao novas tabelas indicadores e acoes
-- Criacao campo pecas_planejamento no projeto
-- Os tipos Responsável Proponente e Patrocinador
-- nao devem fazer parte da lista de papeis de membros de projeto/programas
update public.tipo_papel
	set apagado = true
	where id IN( 3, 4 );

-- Criacao de tabelas para indicadores e acoes - Sprint-24
-- Criação das SEQUENCES (idempotentes)
CREATE SEQUENCE IF NOT EXISTS public.projeto_indicador_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 2147483647
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS public.projeto_acao_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 2147483647
    CACHE 1;

-- Criação da tabela projeto_indicador 
CREATE TABLE IF NOT EXISTS public.projeto_indicador (
    id INTEGER NOT NULL DEFAULT nextval('projeto_indicador_id_seq'::regclass),
    id_projeto INTEGER NOT NULL,
    tipo_indicador VARCHAR(80) NOT NULL,
    descricao_indicador VARCHAR(2000) NOT NULL,
    meta_indicador VARCHAR(2000) NOT NULL,
    criado_em TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP WITHOUT TIME ZONE,
    apagado BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_projeto_indicador PRIMARY KEY (id),
    CONSTRAINT fk_proj_indicador_id_projeto_projeto FOREIGN KEY (id_projeto)
        REFERENCES public.projeto (id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

-- Criação da tabela projeto_acao 
CREATE TABLE IF NOT EXISTS public.projeto_acao (
    id INTEGER NOT NULL DEFAULT nextval('projeto_acao_id_seq'::regclass),
    id_projeto INTEGER NOT NULL,
    acao_principal VARCHAR(2000) NOT NULL,
    valor_estimado NUMERIC(14,2) NOT NULL,
    descricao_acoes_secundarias VARCHAR(2000),
    criado_em TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP WITHOUT TIME ZONE,
    apagado BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_projeto_acao PRIMARY KEY (id),
    CONSTRAINT fk_proj_acao_id_projeto FOREIGN KEY (id_projeto)
        REFERENCES public.projeto (id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

-- Adição da coluna (somente se ainda não existir)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'projeto'
        AND column_name = 'pecas_planejamento'
    ) THEN
        ALTER TABLE public.projeto
        ADD COLUMN pecas_planejamento VARCHAR(2000);
    END IF;
END
$$;


-- 25/06/2025
-- Devido a um BUG ocorrido em producao (SISCAP-338) em que detectamos
-- a nao existencia da coluna id_tipo_status nas tabela prjeto_indicador e 
-- prjeto_acao script foi criado para atualizar a base e evitar o erro

-- Adição da coluna (somente se ainda não existir)
DO $$
BEGIN

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'projeto_indicador'
        AND column_name = 'id_tipo_status'
    ) THEN
	
        ALTER TABLE public.projeto_indicador
        ADD COLUMN id_tipo_status integer NOT NULL DEFAULT 1;
        
        ALTER TABLE public.projeto_indicador
        ADD CONSTRAINT fk_projeto_indicador_id_tipo_status_tipo_status 
        FOREIGN KEY (id_tipo_status)
        REFERENCES public.tipo_status (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION;
		
    END IF;

	IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'projeto_acao'
        AND column_name = 'id_tipo_status'
    ) THEN
	
        ALTER TABLE public.projeto_acao
        ADD COLUMN id_tipo_status integer NOT NULL DEFAULT 1;
        
        ALTER TABLE public.projeto_acao
        ADD CONSTRAINT fk_projeto_acao_id_tipo_status_tipo_status 
        FOREIGN KEY (id_tipo_status)
        REFERENCES public.tipo_status (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION;
		
    END IF;
	
END $$;

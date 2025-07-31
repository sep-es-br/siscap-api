
-- 02/06/2026
-- Atualizar lista de tipo de papeis criacao novas tabelas indicadores e acoes
-- Criacao campo pecas_planejamento no projeto
-- Os tipos Responsável Proponente e Patrocinador
-- nao devem fazer parte da lista de papeis de membros de projeto/programas

-- Adição da coluna (somente se ainda não existir)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'projeto'
        AND column_name = 'protocolo_edocs'
    ) THEN
        ALTER TABLE public.projeto
        ADD COLUMN protocolo_edocs VARCHAR(15);
    END IF;
END
$$;

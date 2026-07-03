-- 1. Adicionar coluna apagado (seguindo padrão do projeto)
ALTER TABLE projeto_indicador_externo_meta
ADD COLUMN apagado BOOLEAN NOT NULL DEFAULT FALSE;

-- 3. Remover coluna antiga
ALTER TABLE projeto_indicador_externo_meta
DROP COLUMN apagado_em;

-- 4. Adicionar coluna atualizado_em 
ALTER TABLE projeto_indicador_externo_meta
ADD COLUMN atualizado_em TIMESTAMP NULL;
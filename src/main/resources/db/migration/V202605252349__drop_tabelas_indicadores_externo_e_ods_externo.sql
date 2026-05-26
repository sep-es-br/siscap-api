-- ============================================
-- REMOVER CONSTRAINTS
-- ============================================
ALTER TABLE indicador_fato_externo
DROP CONSTRAINT IF EXISTS fk_fato_indicador;

ALTER TABLE projeto_indicador
DROP CONSTRAINT IF EXISTS fk_projeto_indicador_indicador_externo;

ALTER TABLE ods_indicador_externo
DROP CONSTRAINT IF EXISTS fk_ods_indicador_externo_indicador;

-- ============================================
-- REMOVER TABELAS
-- Ordem importante por causa das dependências
-- ============================================
DROP TABLE IF EXISTS indicador_fato_externo;

DROP TABLE IF EXISTS ods_indicador_externo;

DROP TABLE IF EXISTS indicador_externo;
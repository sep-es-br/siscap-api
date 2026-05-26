-- remove a foreign key antiga
ALTER TABLE projeto_indicador
DROP CONSTRAINT IF EXISTS fk_projeto_indicador_indicador_externo;

-- garante que a coluna continue existindo como integer simples
ALTER TABLE projeto_indicador
ALTER COLUMN id_indicador_externo TYPE INTEGER;
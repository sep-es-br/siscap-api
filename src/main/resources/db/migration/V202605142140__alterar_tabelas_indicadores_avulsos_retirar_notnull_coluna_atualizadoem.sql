ALTER TABLE indicador_avulso
	ALTER COLUMN atualizado_em DROP NOT NULL;
	
ALTER TABLE indicador_avulso
	ALTER COLUMN base_de_referencia DROP NOT NULL;
	
ALTER TABLE indicador_avulso_meta
	ALTER COLUMN atualizado_em DROP NOT NULL;
	
ALTER TABLE projeto_indicador_avulso
	ALTER COLUMN atualizado_em DROP NOT NULL;

ALTER TABLE projeto_indicador_avulso_meta
	ALTER COLUMN atualizado_em DROP NOT NULL;	
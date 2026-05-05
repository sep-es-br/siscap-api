-- permite que as colunas descricao_meta e meta_indicador sejam nulas, 
--para manter a estrutura atual de projeto_indicador;
ALTER TABLE projeto_indicador
ALTER COLUMN meta_indicador DROP NOT NULL;

ALTER TABLE projeto_indicador
ALTER COLUMN descricao_indicador DROP NOT NULL;
ALTER TABLE projeto_indicador_externo_meta
RENAME COLUMN idFato TO id_fato;

ALTER TABLE projeto_indicador_externo_meta
RENAME COLUMN anoMeta TO ano_meta;

ALTER TABLE projeto_indicador_externo_meta
RENAME COLUMN valorMeta TO valor_meta;
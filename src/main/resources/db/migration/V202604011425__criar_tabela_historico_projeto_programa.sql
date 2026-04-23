CREATE TABLE projeto_programa(
	id SERIAL PRIMARY KEY,
	id_projeto bigint NOT NULL,
	id_programa bigint NOT NULL,
	apagado_em timestamp NULL,
	criado_em timestamp NOT NULL,
	CONSTRAINT fk_projeto_programa_projeto 
		FOREIGN KEY	(id_projeto) 
		REFERENCES projeto (id)
		ON DELETE CASCADE,
	CONSTRAINT fk_projeto_programa_programa
		FOREIGN KEY (id_programa)
		REFERENCES programa (id)
		ON DELETE CASCADE
	
);

CREATE UNIQUE INDEX uq_projeto_programa_ativo
ON projeto_programa (id_projeto, id_programa)
WHERE apagado_em IS NULL;


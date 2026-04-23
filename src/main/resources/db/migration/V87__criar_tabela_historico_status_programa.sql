CREATE TABLE programa_status (
	id SERIAL PRIMARY KEY,
	id_programa INT NOT NULL,
	id_pessoa INT NULL,
	status INT NOT NULL,
	inicio_em TIMESTAMP NOT NULL,
	CONSTRAINT fk_programa
		FOREIGN KEY (id_programa) REFERENCES programa(id)
		ON DELETE CASCADE,
	CONSTRAINT fk_pessoa
		FOREIGN KEY (id_pessoa) REFERENCES pessoa (id)
		ON DELETE SET NULL
		
)
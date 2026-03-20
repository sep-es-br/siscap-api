CREATE TABLE status_projeto (
    id BIGSERIAL PRIMARY KEY,
    id_projeto BIGINT NOT NULL,
    inicio_em TIMESTAMP NOT NULL,
    fim_em TIMESTAMP NULL,
    id_pessoa BIGINT NULL,
    status VARCHAR(50) NOT NULL,

    CONSTRAINT fk_status_projeto_projeto
        FOREIGN KEY (id_projeto)
        REFERENCES projeto (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_status_projeto_pessoa
        FOREIGN KEY (id_pessoa)
        REFERENCES pessoa (id)
);

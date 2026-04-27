ALTER TABLE
    projeto_indicador
ADD
    COLUMN id_indicador_externo integer;

ALTER TABLE
    projeto_indicador
ADD
    CONSTRAINT fk_projeto_indicador_indicador_externo FOREIGN KEY (id_indicador_externo) REFERENCES indicador_externo (id_indicador);
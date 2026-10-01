DO $$
DECLARE
    qtd_registros BIGINT;
BEGIN

    SELECT COUNT(*)
      INTO qtd_registros
      FROM pessoa
     WHERE nome IS NOT NULL
       AND nome <> UPPER(nome);

    IF qtd_registros > 10000 THEN
        RAISE EXCEPTION
            'Migration abortada: % pessoas precisam ser normalizadas.',
            qtd_registros;
    END IF;

    UPDATE pessoa
       SET nome = UPPER(nome)
     WHERE nome IS NOT NULL
       AND nome <> UPPER(nome);

END $$;
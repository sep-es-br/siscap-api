DO $$
DECLARE
    qtd_registros BIGINT;
BEGIN

    SELECT COUNT(*)
      INTO qtd_registros
      FROM projeto
     WHERE protocolo_edocs IS NOT NULL
       AND TRIM(protocolo_edocs) = '';

    IF qtd_registros > 10000 THEN
        RAISE EXCEPTION
            'Migration abortada: % registros possuem protocolo E-Docs vazio e precisam ser normalizados para NULL.',
            qtd_registros;
    END IF;

    UPDATE projeto
       SET protocolo_edocs = NULL
     WHERE protocolo_edocs IS NOT NULL
       AND TRIM(protocolo_edocs) = '';

END $$;
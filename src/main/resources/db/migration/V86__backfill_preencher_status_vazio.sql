UPDATE programa prog
SET status = CASE

    -- 1. RECUSADO
    WHEN EXISTS (
        SELECT 1
        FROM programa_assinatura_documento_edocs ass
        WHERE ass.id_programa = prog.id
          AND ass.status_assinatura = 4
    ) THEN 5

    -- 2. ASSINADO (todas = 2)
    WHEN EXISTS (
        SELECT 1
        FROM programa_assinatura_documento_edocs ass
        WHERE ass.id_programa = prog.id
    )
    AND NOT EXISTS (
        SELECT 1
        FROM programa_assinatura_documento_edocs ass
        WHERE ass.id_programa = prog.id
          AND ass.status_assinatura <> 2
    ) THEN 3

    -- 3. AGUARDANDO ASSINATURAS
    WHEN EXISTS (
        SELECT 1
        FROM programa_assinatura_documento_edocs ass
        WHERE ass.id_programa = prog.id
    ) THEN 2

    -- 4. SEM ASSINATURAS
    WHEN prog.id_documento_edocs IS NULL THEN 1
    ELSE 4

END
WHERE prog.status IS NULL;
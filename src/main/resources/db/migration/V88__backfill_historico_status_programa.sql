INSERT INTO programa_status (id_programa, status, inicio_em)
SELECT id, status, NOW()
FROM programa
WHERE status IS NOT NULL
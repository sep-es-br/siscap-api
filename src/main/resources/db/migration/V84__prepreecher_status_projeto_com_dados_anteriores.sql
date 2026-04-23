INSERT INTO status_projeto (id_projeto, inicio_em, status)
SELECT id, NOW(), status FROM projeto
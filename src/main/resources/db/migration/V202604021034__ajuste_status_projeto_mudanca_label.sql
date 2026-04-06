
UPDATE status_projeto
SET status = CASE
    WHEN status = 'Em Análise' THEN 'Análise'
    WHEN status = 'Em Elaboração' THEN 'Elaboração'
    WHEN status = 'Em Complementação' THEN 'Complementação'
END
WHERE status IN ('Em Análise', 'Em Elaboração', 'Em Complementação');

UPDATE projeto
SET status = CASE
    WHEN status = 'Em Análise' THEN 'Análise'
    WHEN status = 'Em Elaboração' THEN 'Elaboração'
    WHEN status = 'Em Complementação' THEN 'Complementação'
END
WHERE status IN ('Em Análise', 'Em Elaboração', 'Em Complementação');

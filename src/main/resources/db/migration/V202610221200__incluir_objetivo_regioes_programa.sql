ALTER TABLE programa ADD COLUMN objetivo TEXT;

CREATE TABLE programa_regiao_atendida (
    id_programa BIGINT NOT NULL REFERENCES programa(id) ON DELETE CASCADE,
    id_localidade BIGINT NOT NULL REFERENCES localidade(id),
    nome VARCHAR(255) NOT NULL,
    PRIMARY KEY (id_programa, id_localidade)
);

-- Inicializa os novos campos dos Programas existentes a partir dos vínculos ativos.
UPDATE programa programa
SET objetivo = origem.objetivo
FROM (
    SELECT id_programa, STRING_AGG(objetivo, E'\n\n' ORDER BY primeiro_projeto) AS objetivo
    FROM (
        SELECT vinculo.id_programa, TRIM(projeto.objetivo) AS objetivo, MIN(projeto.id) AS primeiro_projeto
        FROM projeto_programa vinculo
        JOIN projeto ON projeto.id = vinculo.id_projeto AND projeto.apagado = FALSE
        WHERE vinculo.apagado_em IS NULL
          AND NULLIF(TRIM(projeto.objetivo), '') IS NOT NULL
        GROUP BY vinculo.id_programa, TRIM(projeto.objetivo)
    ) objetivos
    GROUP BY id_programa
) origem
WHERE programa.id = origem.id_programa;

INSERT INTO programa_regiao_atendida (id_programa, id_localidade, nome)
SELECT DISTINCT vinculo.id_programa, localidade.id,
       CASE WHEN localidade.tipo = 'Estado' THEN 'Todo o Estado' ELSE localidade.nome END
FROM projeto_programa vinculo
JOIN projeto ON projeto.id = vinculo.id_projeto AND projeto.apagado = FALSE
JOIN (
    SELECT acao.id_projeto, rateio.id_localidade
    FROM projeto_acao acao
    JOIN projeto_acao_localidade_quantia rateio ON rateio.id_projeto_acao = acao.id AND rateio.apagado = FALSE
    WHERE acao.apagado = FALSE AND acao.id_tipo_status = 1
    UNION
    SELECT rateio.id_projeto, rateio.id_localidade
    FROM localidade_quantia rateio
    WHERE rateio.apagado = FALSE
      AND NOT EXISTS (
          SELECT 1 FROM projeto_acao acao
          WHERE acao.id_projeto = rateio.id_projeto AND acao.apagado = FALSE AND acao.id_tipo_status = 1
      )
) regioes ON regioes.id_projeto = projeto.id
JOIN localidade ON localidade.id = regioes.id_localidade AND localidade.apagado = FALSE
WHERE vinculo.apagado_em IS NULL;

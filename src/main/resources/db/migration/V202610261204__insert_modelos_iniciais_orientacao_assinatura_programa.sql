
-- ============================================================
-- SISCAP - Carga inicial de modelos de orientação de assinatura
-- Versão inicial: 1
-- ============================================================

-- SUBSTITUIR PELOS GUIDS REAIS DAS LOTAÇÕES
-- Utilizar o formato de string adotado no SISCAP.
-- No Flyway, substituir as variáveis pelos valores reais
-- ou configurar placeholders do Flyway.

INSERT INTO modelo_orientacao_assinatura_programa
    (guid_lotacao, titulo, versao, conteudo, ativo)
VALUES

-- ============================================================
-- 1. SUBCAP - Encaminhamento
-- ============================================================
(
    '515685f7-2aeb-4cc4-a311-8b793297f8fb',
    'Encaminhamento — Subsecretaria de Captação de Recursos',
    1,
    $texto$
Excelentíssimo Senhor Secretário,

Encaminho a proposta de Programa {{programa}}, consolidada a partir do(s) projeto(s) {{siglasProjetos}}, de iniciativa do(a) {{orgaoProponente}}, com valor estimado de {{valorEstimado}}, já submetida às análises de conformidade estratégica (SUBEPP) e orçamentária (SUBEO), declarada elegível para pleito de financiamento pela Gerência de Captação de Recursos e ratificada por esta Subsecretaria, conforme Parecer de Elegibilidade nº {{numeroParecerElegibilidade}}, anexo.

Proponho a Vossa Excelência o endosso da proposta institucionalmente consolidada e sua submissão ao Governador do Estado, com vistas à autorização para a continuidade dos procedimentos de prospecção de fontes de financiamento, nos termos dos itens 6.6 e 6.7 da Norma de Procedimento SCR nº 001.

$texto$,
    TRUE
),

-- ============================================================
-- 2. SEP - Endosso
-- ============================================================
(
    '07a0462f-6b02-4b98-b86d-18d7e4fa543a',
    'Endosso — Secretaria de Economia e Planejamento',
    1,
    $texto$
Em atendimento ao item 6.6, alínea "c", da Norma de Procedimento SCR nº 001, tomo ciência da declaração de Elegibilidade nº {{numeroDeclaracaoElegibilidade}}, anexo e endosso a Proposta de Programa {{programa}}, consolidada a partir do(s) projeto(s) {{siglasProjetos}}, de iniciativa do(a) {{orgaoProponente}}, com valor estimado de {{valorEstimado}}, institucionalmente consolidada, para fins de continuidade dos procedimentos de captação.

$texto$,
    TRUE
),

-- ============================================================
-- 3. GOVERNADOR - Decisão
-- ============================================================
(
    '74f2a63f-c5cf-4a81-91d9-5643c37367ec',
    'Decisão — Governador do Estado',
    1,
    $texto$
Em atendimento ao item 6.7 da Norma de Procedimento SCR nº 001, autorizo a continuidade dos procedimentos com vistas à prospecção de fontes de financiamento para o Programa {{programa}}, consolidada a partir do(s) projeto(s) {{siglasProjetos}}, de iniciativa do(a) {{orgaoProponente}}, com valor estimado de {{valorEstimado}}, declarada elegível para pleito de financiamento pela Gerência de Captação de Recursos e ratificada por esta Subsecretaria, conforme Parecer de Elegibilidade nº {{numeroParecerElegibilidade}}, anexo.

$texto$,
    TRUE
);

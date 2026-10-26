
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
    'GUID_LOTACAO_SUBCAP',
    'Encaminhamento — Subsecretaria de Captação de Recursos',
    1,
    $texto$
Excelentíssimo Senhor Secretário,

Encaminho a proposta de Programa {{programa}}, consolidada a partir do(s) projeto(s) {{siglasProjetos}}, de iniciativa do(a) {{orgaoProponente}}, com valor estimado de {{valorEstimado}}, já submetida às análises de conformidade estratégica (SUBEPP) e orçamentária (SUBEO), declarada elegível para pleito de financiamento pela Gerência de Captação de Recursos e ratificada por esta Subsecretaria, conforme Parecer de Elegibilidade nº {{numeroParecerElegibilidade}}, anexo.

Proponho a Vossa Excelência o endosso da proposta institucionalmente consolidada e sua submissão ao Governador do Estado, com vistas à autorização para a continuidade dos procedimentos de prospecção de fontes de financiamento, nos termos dos itens 6.6 e 6.7 da Norma de Procedimento SCR nº 001.

{{local}}, {{dataAssinatura}}.

{{nomeAssinante}}

Subsecretária de Captação de Recursos – SUBCAP/SEP
$texto$,
    TRUE
),

-- ============================================================
-- 2. SEP - Endosso
-- ============================================================
(
    'GUID_LOTACAO_SEP',
    'Endosso — Secretaria de Economia e Planejamento',
    1,
    $texto$
Em atendimento ao item 6.6, alínea "c", da Norma de Procedimento SCR nº 001, tomo ciência da declaração de Elegibilidade nº {{numeroDeclaracaoElegibilidade}}, anexo e endosso a Proposta de Programa {{programa}}, consolidada a partir do(s) projeto(s) {{siglasProjetos}}, de iniciativa do(a) {{orgaoProponente}}, com valor estimado de {{valorEstimado}}, institucionalmente consolidada, para fins de continuidade dos procedimentos de captação.

{{local}}, {{dataAssinatura}}.

{{nomeAssinante}}

Secretário de Estado de Economia e Planejamento – SEP
$texto$,
    TRUE
),

-- ============================================================
-- 3. GOVERNADOR - Decisão
-- ============================================================
(
    'GUID_LOTACAO_GOVERNADOR',
    'Decisão — Governador do Estado',
    1,
    $texto$
Em atendimento ao item 6.7 da Norma de Procedimento SCR nº 001, autorizo a continuidade dos procedimentos com vistas à prospecção de fontes de financiamento para o Programa {{programa}}, consolidada a partir do(s) projeto(s) {{siglasProjetos}}, de iniciativa do(a) {{orgaoProponente}}, com valor estimado de {{valorEstimado}}, declarada elegível para pleito de financiamento pela Gerência de Captação de Recursos e ratificada por esta Subsecretaria, conforme Parecer de Elegibilidade nº {{numeroParecerElegibilidade}}, anexo.

{{local}}, {{dataAssinatura}}.

{{nomeAssinante}}

Governador do Estado do Espírito Santo
$texto$,
    TRUE
);

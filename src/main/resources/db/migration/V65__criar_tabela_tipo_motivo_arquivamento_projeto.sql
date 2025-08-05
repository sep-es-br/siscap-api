-- 31/07/2025
-- Criacao de nova tabela de tipos que faz o mapeamento dos motivos provaveis para arquivamento de um 
-- projeto;
create table tipo_motivo_arquivamento
(
    id            integer primary key,
    tipo          varchar(255) not null,
	codigo		  char(4) not null,
	observacao	  varchar(255) not null,
    criado_em     timestamp,
    atualizado_em timestamp,
    apagado       boolean
);

INSERT INTO tipo_motivo_arquivamento (id, tipo, codigo, observacao, criado_em, atualizado_em, apagado)
VALUES (1, 'Inconsistência nas informações do projeto', 'M01', 'Dados divergentes, ausência de clareza nos objetivos ou metas conflitantes.', CURRENT_TIMESTAMP, NULL, false);

INSERT INTO tipo_motivo_arquivamento (id, tipo, codigo, observacao, criado_em, atualizado_em, apagado)
VALUES (2, 'Projeto fora do escopo institucional', 'M02', 'Não se enquadra nas linhas de atuação previstas pela política pública vigente.', CURRENT_TIMESTAMP, NULL, false);

INSERT INTO tipo_motivo_arquivamento (id, tipo, codigo, observacao, criado_em, atualizado_em, apagado)
VALUES (3, 'Documentação incompleta ou não apresentada', 'M03', 'Ausência de documentos obrigatórios, como estudos técnicos, pareceres, etc.', CURRENT_TIMESTAMP, NULL, false);

INSERT INTO tipo_motivo_arquivamento (id, tipo, codigo, observacao, criado_em, atualizado_em, apagado)
VALUES (4, 'Sobreposição com projetos já existentes', 'M04', 'Ação já contemplada por outro projeto aprovado ou em execução.', CURRENT_TIMESTAMP, NULL, false);

INSERT INTO tipo_motivo_arquivamento (id, tipo, codigo, observacao, criado_em, atualizado_em, apagado)
VALUES (5, 'Não atendimento aos critérios mínimos da SUBCAP', 'M05', 'Não alcança critérios técnicos mínimos de viabilidade ou prioridade.', CURRENT_TIMESTAMP, NULL, false);

INSERT INTO tipo_motivo_arquivamento (id, tipo, codigo, observacao, criado_em, atualizado_em, apagado)
VALUES (6, 'Solicitação de arquivamento pelo proponente', 'M06', 'Pedido formal de desistência do projeto pelo próprio autor.', CURRENT_TIMESTAMP, NULL, false);

INSERT INTO tipo_motivo_arquivamento (id, tipo, codigo, observacao, criado_em, atualizado_em, apagado)
VALUES (7, 'Ausência de retorno após prazo para ajustes', 'M07', 'Proponente não respondeu dentro do prazo estipulado para revisão do DIC.', CURRENT_TIMESTAMP, NULL, false);

INSERT INTO tipo_motivo_arquivamento (id, tipo, codigo, observacao, criado_em, atualizado_em, apagado)
VALUES (8, 'Projeto obsoleto ou desatualizado', 'M08', 'Contexto, dados ou proposta já não são mais válidos ou atuais.', CURRENT_TIMESTAMP, NULL, false);

INSERT INTO tipo_motivo_arquivamento (id, tipo, codigo, observacao, criado_em, atualizado_em, apagado)
VALUES (9, 'Projeto duplicado no sistema', 'M09', 'Cadastrado mais de uma vez por erro ou tentativa de reenvio.', CURRENT_TIMESTAMP, NULL, false);

INSERT INTO tipo_motivo_arquivamento (id, tipo, codigo, observacao, criado_em, atualizado_em, apagado)
VALUES (10, 'Orientação superior para não continuidade', 'M10', 'Decisão estratégica ou normativa de instâncias superiores.', CURRENT_TIMESTAMP, NULL, false);

INSERT INTO tipo_motivo_arquivamento (id, tipo, codigo, observacao, criado_em, atualizado_em, apagado)
VALUES (11, 'Outros', 'M11', '', CURRENT_TIMESTAMP, NULL, false);

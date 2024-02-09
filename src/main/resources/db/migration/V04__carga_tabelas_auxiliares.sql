INSERT INTO public.status (id, status, criado_em, atualizado_em, apagado)
VALUES (1, 'ATIVO', '2024-02-08 15:12:27.000000', null, false);

INSERT INTO public.tipo_documento (id, titulo, sigla, criado_em, atualizado_em, apagado)
VALUES (1, 'Cadastro de Pessoa Física', 'CPF', '2024-02-08 15:12:27.000000', null, false);
INSERT INTO public.tipo_documento (id, titulo, sigla, criado_em, atualizado_em, apagado)
VALUES (2, 'Carteira Nacional de Habilitação', 'CNH', '2024-02-08 15:12:27.000000', null, false);

INSERT INTO public.tipo_entidade (id, tipo, criado_em, atualizado_em, apagado)
VALUES (1, 'Governamental', '2024-02-08 15:12:27.000000', null, false);
INSERT INTO public.tipo_entidade (id, tipo, criado_em, atualizado_em, apagado)
VALUES (2, 'Financeira', '2024-02-08 15:12:27.000000', null, false);

SELECT SETVAL((SELECT PG_GET_SERIAL_SEQUENCE('"status"', 'id')), (SELECT (MAX("id") + 1) FROM "status"), FALSE);
SELECT SETVAL((SELECT PG_GET_SERIAL_SEQUENCE('"tipo_documento"', 'id')), (SELECT (MAX("id") + 1) FROM "tipo_documento"), FALSE);
SELECT SETVAL((SELECT PG_GET_SERIAL_SEQUENCE('"tipo_entidade"', 'id')), (SELECT (MAX("id") + 1) FROM "tipo_entidade"), FALSE);
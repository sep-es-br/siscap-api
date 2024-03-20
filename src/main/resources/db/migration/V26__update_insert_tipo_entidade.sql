update tipo_entidade
set tipo = 'Instituição Pública'
where id = 1;
update tipo_entidade
set tipo = 'Instituição Financeira'
where id = 2;

INSERT INTO public.tipo_entidade (id, tipo, criado_em, atualizado_em, apagado)
VALUES (3, 'Autarquia', '2024-02-08 15:12:27.000000', null, false);
INSERT INTO public.tipo_entidade (id, tipo, criado_em, atualizado_em, apagado)
VALUES (4, 'Empresa Privada', '2024-02-08 15:12:27.000000', null, false);
INSERT INTO public.tipo_entidade (id, tipo, criado_em, atualizado_em, apagado)
VALUES (5, 'Organização Não Governamental', '2024-02-08 15:12:27.000000', null, false);
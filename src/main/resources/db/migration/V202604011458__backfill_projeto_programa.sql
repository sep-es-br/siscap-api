INSERT INTO public.projeto_programa(
	id_projeto, id_programa, criado_em
) SELECT id, id_programa, NOW() FROM projeto
	WHERE id_programa IS NOT NULL;

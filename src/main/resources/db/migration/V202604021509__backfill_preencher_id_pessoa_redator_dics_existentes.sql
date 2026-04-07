
UPDATE projeto p
SET id_pessoa_redator = sub.id_pessoa
FROM (
    SELECT DISTINCT ON (id_projeto)
           id_projeto,
           id_pessoa
    FROM projeto_pessoa
    WHERE id_tipo_papel = 3
    ORDER BY id_projeto, id -- ou data_criacao DESC, se existir
) sub
WHERE sub.id_projeto = p.id and
	id_pessoa_redator IS NULL
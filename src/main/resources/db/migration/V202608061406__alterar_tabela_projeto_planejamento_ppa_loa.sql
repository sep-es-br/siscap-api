
-- mudar nullidade dos campos de acoes de planejamento do projeto
ALTER TABLE projeto_planejamento_ppa_loa
    ALTER COLUMN id_projeto SET NOT NULL,
    ALTER COLUMN cod_acao SET NOT NULL,
    ALTER COLUMN cod_funcao SET NOT NULL,
    ALTER COLUMN cod_programa SET NOT NULL,
    ALTER COLUMN ano SET NOT NULL,
    ALTER COLUMN cod_uo SET NOT NULL;

-- cria um index unico para campos de acao evitando duplicidade no banco
CREATE UNIQUE INDEX uk_projeto_planejamento_ppa_loa
ON projeto_planejamento_ppa_loa (
    id_projeto,
    cod_acao,
    cod_funcao,
    cod_programa,
    ano,
    cod_uo
);

-- criando campo novo no projeto para indentificar que nao haviam acoes de planejamento para
-- inclusao;
ALTER TABLE projeto
    ADD COLUMN nao_previsto_ppa boolean NOT NULL DEFAULT FALSE;
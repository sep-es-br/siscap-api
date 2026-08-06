-- tivemos que ajustar o indice qdo uma acao foi excluida do projeto
-- mas incluimos novamente
DROP INDEX IF EXISTS uk_projeto_planejamento_ppa_loa;

CREATE UNIQUE INDEX uk_projeto_planejamento_ppa_loa
    ON projeto_planejamento_ppa_loa (
        id_projeto,
        cod_acao,
        cod_funcao,
        cod_programa,
        ano,
        cod_uo
    )
    WHERE apagado = FALSE;
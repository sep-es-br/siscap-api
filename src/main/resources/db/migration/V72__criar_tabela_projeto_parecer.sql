-- 14/10/2025
-- Criacao de uma tabela para armazenar por projeto
-- os pareceres das áreas SUBEP e SUBEO
create table projeto_parecer (
    id serial constraint pk_projeto_parecer primary key,
    id_projeto integer not null constraint fk_proj_parecer_id_projeto_projeto references projeto(id),
    guid_unidade_organizacao VARCHAR(50) NOT NULL,
    texto_parecer VARCHAR(2000) NOT NULL,
    status_parecer integer not null,
    data_envio timestamp,
    guid_documento_edocs VARCHAR(50),
    criado_em timestamp not null default current_timestamp,
    atualizado_em timestamp,
    apagado boolean not null default false
)
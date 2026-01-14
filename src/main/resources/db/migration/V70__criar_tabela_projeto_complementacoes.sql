-- 30/09/2025
-- Criacao de uma tabela para armazenar por projeto
-- a lista de campos e o que deve ser modificado nesses campoas
-- quando a SUBCAP envia um DIC já autuado no E-Docs para complementação

create table projeto_complemento
(
    id serial     constraint pk_projeto_complemento primary key,
    id_projeto    integer not null  constraint fk_proj_complemento_id_projeto_projeto references projeto(id),
    campo         VARCHAR(100) NOT NULL, 
    mensagem_complementacao    VARCHAR(2000) NOT NULL,
    criado_em     timestamp    not null default current_timestamp,
    atualizado_em timestamp,
    apagado       boolean      not null default false
)
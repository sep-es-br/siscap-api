-- 08/10/2024 - Criação da tabela de entidade 'cartaconsulta'
-- Entidade só pode estar vinculada a um objeto por vez: Programa ou Projeto
-- nome_documento: referencia do arquivo contendo o corpo da carta consulta
-- id_operacao: tipo de operação que a carta consulta se refere (tabela 'tipo_operacao')

create table cartaconsulta
(
    id             serial primary key,
    nome_documento varchar unique not null,
    id_projeto     integer references projeto (id),
    id_programa    integer references programa (id),
    id_operacao    integer        not null references tipo_operacao (id),
    criado_em      timestamp      not null default current_timestamp,
    atualizado_em  timestamp,
    apagado        boolean        not null default false,
    check (
        (id_projeto is not null and id_programa is null) or
        (id_projeto is null and id_programa is not null)
        )
);
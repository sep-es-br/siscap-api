-- 15/12/2025
-- Criacao nova tabela relacionada a carta_consulta ( pesquisa )
-- para registrar as organizações financeiras destinatários da pesquisa de financiamento 
create table cartaconsulta_destinatario
(
    id               serial primary key,
    id_cartaconsulta integer      not null references cartaconsulta,
    id_organizacao   integer      not null references organizacao,
    criado_em        timestamp    not null default current_timestamp,
    atualizado_em    timestamp,
    apagado          bool         not null default false
);
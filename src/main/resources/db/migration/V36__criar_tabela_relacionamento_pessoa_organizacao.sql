create table pessoa_organizacao
(
    id             serial
        constraint pk_pessoa_organizacao
            primary key,
    id_pessoa      integer not null
        constraint fk_pessoa_organizacao_id_pessoa_pessoa
            references pessoa,
    id_organizacao integer not null
        constraint fk_pessoa_organizacao_id_organizacao_organizacao
            references organizacao,
    responsavel    boolean,
    data_inicio    timestamp,
    data_fim       timestamp,
    criado_em      timestamp,
    atualizado_em  timestamp,
    apagado        boolean
);

insert into pessoa_organizacao
(id_pessoa,
 id_organizacao,
 responsavel,
 data_inicio,
 data_fim,
 criado_em,
 atualizado_em,
 apagado)

select p.id,
       p.id_organizacao,
       FALSE,
       coalesce(p.criado_em, p.atualizado_em),
       NULL,
       CURRENT_TIMESTAMP,
       NULL,
       FALSE
from pessoa p
         join organizacao o on p.id_organizacao = o.id;
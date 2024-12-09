alter table projeto_pessoa add column id_papel integer;
alter table projeto_pessoa add constraint fk_projeto_pessoa_id_papel_papel foreign key (id_papel) references papel(id);

alter table projeto_pessoa add column id_equipe integer;
alter table projeto_pessoa add constraint fk_projeto_pessoa_id_equipe_equipe foreign key (id_equipe) references equipe(id);

alter table projeto_pessoa add column id_status integer;
alter table projeto_pessoa add constraint fk_projeto_pessoa_id_status_status foreign key (id_status) references status(id);

alter table projeto_pessoa add column data_inicio timestamp;
alter table projeto_pessoa add column data_fim timestamp;
alter table projeto_pessoa add column criado_em timestamp;
alter table projeto_pessoa add column atualizado_em timestamp;
alter table projeto_pessoa add column apagado boolean;

with projeto_pessoa_update_data as (
    select
        proj_pess.id,
        proj.criado_em as data_inicio_projeto,
        proj.criado_em as criado_em_projeto
    from
        projeto_pessoa proj_pess
            inner join projeto proj on proj_pess.id_projeto = proj.id
)
update projeto_pessoa
set
    id_papel = 2,
    id_equipe = 1,
    id_status = 1,
    data_inicio = projeto_pessoa_update_data.data_inicio_projeto,
    data_fim = NULL,
    criado_em = projeto_pessoa_update_data.criado_em_projeto,
    atualizado_em = NULL,
    apagado = FALSE
from projeto_pessoa_update_data
where projeto_pessoa.id = projeto_pessoa_update_data.id;
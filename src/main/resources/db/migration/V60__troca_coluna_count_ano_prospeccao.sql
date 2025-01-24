-- 23/01/2025
-- Substitui coluna "count_ano" da tabela "prospeccao" por "contagem_cc"
-- Propósito é configurar propriedade "codigoProspeccao" dos DTOs
-- ProspeccaoDetalhesDto e ProspeccaoListaDTO de acordo com a quantidade
-- de prospeccções vinculadas á aquela carta consulta anteriormente

alter table prospeccao
    drop column if exists count_ano;

alter table prospeccao
    add column contagem_cc varchar(255) not null default '0000';

with prospeccao_contagem_cc as (select tempProsp.id                                                         as id_prospeccao,
                                       row_number()
                                       over (partition by tempProsp.id_cartaconsulta order by tempProsp.id) as count_incremental
                                from prospeccao tempProsp
                                order by tempProsp.id)
update prospeccao
set contagem_cc = trim(lpad(to_char(prospeccao_contagem_cc.count_incremental, '0000'), 5) || '/' ||
                       date_part('year', prospeccao.criado_em))
from prospeccao_contagem_cc
where prospeccao_contagem_cc.id_prospeccao = prospeccao.id;

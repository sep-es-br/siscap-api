-- 08/07/2025
-- Cria coluna "count_ano" nas tabelas "projeto", "programa" e "prospeccao"

-- Cria coluna "count_ano" na tabela "projeto"
alter table projeto
    add column count_ano varchar(255) not null default '0000';

-- Insere valores iniciais de "count_ano" para "projeto" para o ano de 2024
with projeto_count_ano as (select proj.id,
                                  trim(lpad(to_char(proj.id, '0000'), 5) || '/' ||
                                       date_part('year', proj.criado_em)) as count_ano
                           from projeto proj
                           where date_part('year', proj.criado_em) = '2024')
update projeto
set count_ano = projeto_count_ano.count_ano
from projeto_count_ano
where projeto_count_ano.id = projeto.id;

-- Cria coluna "count_ano" na tabela "programa"
alter table programa
    add column count_ano varchar(255) not null default '0000';

-- Insere valores iniciais de "count_ano" para "programa" para o ano de 2024
with programa_count_ano as (select prog.id,
                                   trim(lpad(to_char(prog.id, '0000'), 5) || '/' ||
                                        date_part('year', prog.criado_em)) as count_ano
                            from programa prog
                            where date_part('year', prog.criado_em) = '2024')
update programa
set count_ano = programa_count_ano.count_ano
from programa_count_ano
where programa_count_ano.id = programa.id;

-- Cria coluna "count_ano" na tabela "prospeccao"
alter table prospeccao
    add column count_ano varchar(255) not null default '0000';

-- Insere valores iniciais de "count_ano" para "prospeccao" para o ano de 2024
with prospeccao_count_ano as (select pros.id,
                                     trim(lpad(to_char(pros.id, '0000'), 5) || '/' ||
                                          date_part('year', pros.criado_em)) as count_ano
                              from prospeccao pros
                              where date_part('year', pros.criado_em) = '2024')
update prospeccao
set count_ano = prospeccao_count_ano.count_ano
from prospeccao_count_ano
where prospeccao_count_ano.id = prospeccao.id;


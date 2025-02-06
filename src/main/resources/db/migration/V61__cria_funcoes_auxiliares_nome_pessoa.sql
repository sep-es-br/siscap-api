-- 06/02/2025
-- Cria no schema 'public' funções auxiliares para tratamento e comparacao de
-- nomes de pessoas. Especificamente a coluna "nome" de uma entidade da tabela "pessoa".

-- Função tratar_nome_pessoa
drop function if exists public.tratar_nome_pessoa(text);

create or replace function public.tratar_nome_pessoa(nome text)
    returns text[]
    language plpgsql
as
$function$
declare
    delimitador text := '\s+';
begin
    return regexp_split_to_array(unaccent(lower(nome)), delimitador);
end;
$function$
;

comment on function public.tratar_nome_pessoa(text) is
    'Transforma todas as letras do nome da pessoa em minúsculas, substitui caracteres diacríticos (acentos) pelo equivalente utilizando extensão PostgreSQL "unaccent" e separa o nome em um array de dados do tipo ''text'' pelo delimitador usando RegExp.
Utilizado para alimentar função ''comparar_nome_pessoa''.';

-- Função comparar_nome_pessoa
drop function if exists public.comparar_nome_pessoa(text, text);

create or replace function public.comparar_nome_pessoa(nome_pessoa text, nome_comparacao text)
    returns boolean
    language plpgsql
as
$function$
begin
    return tratar_nome_pessoa(nome_pessoa) <@ tratar_nome_pessoa(nome_comparacao);
end;
$function$
;

comment on function public.comparar_nome_pessoa(text, text) is
    'Chama função interna ''tratar_nome_pessoa'' para transformar os argumentos do tipo ''text'' em um array de elementos do tipo ''text'', retornando valor ''boolean'' verificando se os elementos do array composto pelo nome da pessoa estão contidos dentro do array composto pelo nome de comparação fornecido.'
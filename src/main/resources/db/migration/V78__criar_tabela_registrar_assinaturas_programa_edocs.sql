-- 27/01/2026
-- criar tabela que vai armazenar as assinaturas que devem ser 
-- colhidas no pdf do programa via edocs
create table programa_assinatura_documento_edocs (
    id serial constraint pk_programa_assinatura_documento_edocs primary key,
    id_programa integer not null constraint fk_prog_assinatura_id_programa_programa references programa(id),
    id_pessoa integer not null constraint fk_prog_assinatura_id_pessoa_pessoa references pessoa(id),
    status_assinatura integer not null,
    data_assinatura timestamp,
    criado_em timestamp not null default current_timestamp,
    atualizado_em timestamp,
    apagado boolean not null default false
);

-- alterar tabela do programa para criar colunas armazenar
-- id do documento a ser assinado no edocs
-- id do processo no edocs e protocolo no edocs
ALTER TABLE public.programa
    ADD COLUMN protocolo_edocs character varying(15);

ALTER TABLE public.programa
    ADD COLUMN id_documento_edocs character varying(50);

ALTER TABLE public.programa
    ADD COLUMN id_processo_edocs character varying(50);

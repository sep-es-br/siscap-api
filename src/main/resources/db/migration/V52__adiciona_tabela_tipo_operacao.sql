-- 01/10/2024 - Criação da tabela "tipo_operacao" para alimentar valores de carta consulta

create table tipo_operacao
(
    id            serial primary key,
    tipo          varchar(255) not null,
    criado_em     timestamp    not null default current_timestamp,
    atualizado_em timestamp,
    apagado       boolean      not null default false
);

insert into tipo_operacao (tipo)
values ('Operação de Crédito Interno');
insert into tipo_operacao (tipo)
values ('Operação de Crédito Externo');
insert into tipo_operacao (tipo)
values ('Garantia de Crédito');
insert into tipo_operacao (tipo)
values ('Emenda Parlamentar');
insert into tipo_operacao (tipo)
values ('Convênios');
insert into tipo_operacao (tipo)
values ('Parcerias Público-Privadas (PPP)');
insert into tipo_operacao (tipo)
values ('Transferências Voluntárias');
insert into tipo_operacao (tipo)
values ('Doações e Patrocínios');
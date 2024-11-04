-- 16/10/2024
-- Cria tabela "localidade", com o propósito de centralizar lógica de rateio do valor
-- do projeto e granularizar o menor nivel de dado da aplicação aqui.

create table localidade
(
    id             serial primary key,
    localidade_pai integer
        constraint fk_localidade_localidade_pai_localidade references localidade,
    nome           varchar(255) not null,
    tipo           varchar(255) not null,
    criado_em      timestamp    not null default current_timestamp,
    atualizado_em  timestamp,
    apagado        boolean      not null default true
);

insert into localidade (id, localidade_pai, nome, tipo, criado_em, atualizado_em, apagado)
values (1, null, 'Espirito Santo', 'Estado', current_timestamp, null, false),
       (2, 1, 'Metropolitana', 'Microrregiao', current_timestamp, null, false),
       (3, 1, 'Central Serrana', 'Microrregiao', current_timestamp, null, false),
       (4, 1, 'Sudoeste Serrana', 'Microrregiao', current_timestamp, null, false),
       (5, 1, 'Litoral Sul', 'Microrregiao', current_timestamp, null, false),
       (6, 1, 'Central Sul', 'Microrregiao', current_timestamp, null, false),
       (7, 1, 'Caparaó', 'Microrregiao', current_timestamp, null, false),
       (8, 1, 'Rio Doce', 'Microrregiao', current_timestamp, null, false),
       (9, 1, 'Centro-Oeste', 'Microrregiao', current_timestamp, null, false),
       (10, 1, 'Nordeste', 'Microrregiao', current_timestamp, null, false),
       (11, 1, 'Noroeste', 'Microrregiao', current_timestamp, null, false),
       (12, 2, 'Cariacica', 'Municipio', current_timestamp, null, false),
       (13, 2, 'Fundão', 'Municipio', current_timestamp, null, false),
       (14, 2, 'Guarapari', 'Municipio', current_timestamp, null, false),
       (15, 2, 'Serra', 'Municipio', current_timestamp, null, false),
       (16, 2, 'Viana', 'Municipio', current_timestamp, null, false),
       (17, 2, 'Vila Velha', 'Municipio', current_timestamp, null, false),
       (18, 2, 'Vitória', 'Municipio', current_timestamp, null, false),
       (19, 3, 'Itaguaçu', 'Municipio', current_timestamp, null, false),
       (20, 3, 'Itarana', 'Municipio', current_timestamp, null, false),
       (21, 3, 'Santa Leopoldina', 'Municipio', current_timestamp, null, false),
       (22, 3, 'Santa Maria de Jetibá', 'Municipio', current_timestamp, null, false),
       (23, 3, 'Santa Teresa', 'Municipio', current_timestamp, null, false),
       (24, 4, 'Afonso Cláudio', 'Municipio', current_timestamp, null, false),
       (25, 4, 'Brejetuba', 'Municipio', current_timestamp, null, false),
       (26, 4, 'Conceição do Castelo', 'Municipio', current_timestamp, null, false),
       (27, 4, 'Domingos Martins', 'Municipio', current_timestamp, null, false),
       (28, 4, 'Laranja da Terra', 'Municipio', current_timestamp, null, false),
       (29, 4, 'Marechal Floriano', 'Municipio', current_timestamp, null, false),
       (30, 4, 'Venda Nova do Imigrante', 'Municipio', current_timestamp, null, false),
       (31, 5, 'Alfredo Chaves', 'Municipio', current_timestamp, null, false),
       (32, 5, 'Anchieta', 'Municipio', current_timestamp, null, false),
       (33, 5, 'Iconha', 'Municipio', current_timestamp, null, false),
       (34, 5, 'Itapemirim', 'Municipio', current_timestamp, null, false),
       (35, 5, 'Marataízes', 'Municipio', current_timestamp, null, false),
       (36, 5, 'Piúma', 'Municipio', current_timestamp, null, false),
       (37, 5, 'Presidente Kennedy', 'Municipio', current_timestamp, null, false),
       (38, 5, 'Rio Novo do Sul', 'Municipio', current_timestamp, null, false),
       (39, 6, 'Apiacá', 'Municipio', current_timestamp, null, false),
       (40, 6, 'Atílio Vivácqua', 'Municipio', current_timestamp, null, false),
       (41, 6, 'Cachoeiro de Itapemirim', 'Municipio', current_timestamp, null, false),
       (42, 6, 'Castelo', 'Municipio', current_timestamp, null, false),
       (43, 6, 'Mimoso do Sul', 'Municipio', current_timestamp, null, false),
       (44, 6, 'Muqui', 'Municipio', current_timestamp, null, false),
       (45, 6, 'Vargem Alta', 'Municipio', current_timestamp, null, false),
       (46, 7, 'Alegre', 'Municipio', current_timestamp, null, false),
       (47, 7, 'Bom Jesus do Norte', 'Municipio', current_timestamp, null, false),
       (48, 7, 'Divino de São Lourenço', 'Municipio', current_timestamp, null, false),
       (49, 7, 'Dores do Rio Preto', 'Municipio', current_timestamp, null, false),
       (50, 7, 'Guaçuí', 'Municipio', current_timestamp, null, false),
       (51, 7, 'Ibatiba', 'Municipio', current_timestamp, null, false),
       (52, 7, 'Ibitirama', 'Municipio', current_timestamp, null, false),
       (53, 7, 'Irupi', 'Municipio', current_timestamp, null, false),
       (54, 7, 'Iúna', 'Municipio', current_timestamp, null, false),
       (55, 7, 'Jerônimo Monteiro', 'Municipio', current_timestamp, null, false),
       (56, 7, 'Muniz Freire', 'Municipio', current_timestamp, null, false),
       (57, 7, 'São José do Calçado', 'Municipio', current_timestamp, null, false),
       (58, 8, 'Aracruz', 'Municipio', current_timestamp, null, false),
       (59, 8, 'Ibiraçu', 'Municipio', current_timestamp, null, false),
       (60, 8, 'João Neiva', 'Municipio', current_timestamp, null, false),
       (61, 8, 'Linhares', 'Municipio', current_timestamp, null, false),
       (62, 8, 'Rio Bananal', 'Municipio', current_timestamp, null, false),
       (63, 8, 'Sooretama', 'Municipio', current_timestamp, null, false),
       (64, 9, 'Alto Rio Novo', 'Municipio', current_timestamp, null, false),
       (65, 9, 'Baixo Guandu', 'Municipio', current_timestamp, null, false),
       (66, 9, 'Colatina', 'Municipio', current_timestamp, null, false),
       (67, 9, 'Governador Lindenberg', 'Municipio', current_timestamp, null, false),
       (68, 9, 'Marilândia', 'Municipio', current_timestamp, null, false),
       (69, 9, 'Pancas', 'Municipio', current_timestamp, null, false),
       (70, 9, 'São Domingos do Norte', 'Municipio', current_timestamp, null, false),
       (71, 9, 'São Gabriel da Palha', 'Municipio', current_timestamp, null, false),
       (72, 9, 'São Roque do Canaã', 'Municipio', current_timestamp, null, false),
       (73, 9, 'Vila Valério', 'Municipio', current_timestamp, null, false),
       (74, 10, 'Boa Esperança', 'Municipio', current_timestamp, null, false),
       (75, 10, 'Conceição da Barra', 'Municipio', current_timestamp, null, false),
       (76, 10, 'Jaguaré', 'Municipio', current_timestamp, null, false),
       (77, 10, 'Montanha', 'Municipio', current_timestamp, null, false),
       (78, 10, 'Mucurici', 'Municipio', current_timestamp, null, false),
       (79, 10, 'Pedro Canário', 'Municipio', current_timestamp, null, false),
       (80, 10, 'Pinheiros', 'Municipio', current_timestamp, null, false),
       (81, 10, 'Ponto Belo', 'Municipio', current_timestamp, null, false),
       (82, 10, 'São Mateus', 'Municipio', current_timestamp, null, false),
       (83, 11, 'Água Doce do Norte', 'Municipio', current_timestamp, null, false),
       (84, 11, 'Águia Branca', 'Municipio', current_timestamp, null, false),
       (85, 11, 'Barra de São Francisco', 'Municipio', current_timestamp, null, false),
       (86, 11, 'Ecoporanga', 'Municipio', current_timestamp, null, false),
       (87, 11, 'Mantenópolis', 'Municipio', current_timestamp, null, false),
       (88, 11, 'Nova Venécia', 'Municipio', current_timestamp, null, false),
       (89, 11, 'Vila Pavão', 'Municipio', current_timestamp, null, false);

-- Renomeia tabelas auxiliares adicionando prefixo "tipo_"
-- Tabela "equipe"
-- Nome da Tabela
alter table equipe
    rename to tipo_equipe;
-- Nome da constraint de primary key
alter table tipo_equipe
    rename constraint equipe_pkey to pk_tipo_equipe;
-- Nome da sequência
alter sequence equipe_id_seq rename to tipo_equipe_id_seq;
-- Tabelas estrangeiras (nome da referência da foreign key e nome das colunas pertinentes)
alter table projeto_pessoa
    rename column id_equipe to id_tipo_equipe;
alter table projeto_pessoa
    rename constraint fk_projeto_pessoa_id_equipe_equipe to fk_projeto_pessoa_id_tipo_equipe_tipo_equipe;
alter table programa_pessoa
    rename column id_equipe to id_tipo_equipe;
alter table programa_pessoa
    rename constraint fk_programa_pessoa_id_equipe_equipe to fk_programa_pessoa_id_tipo_equipe_tipo_equipe;

-- Tabela "papel"
-- Nome da Tabela
alter table papel
    rename to tipo_papel;
-- Nome da constraint de primary key
alter table tipo_papel
    rename constraint papel_pkey to pk_tipo_papel;
-- Nome da sequência
alter sequence papel_id_seq rename to tipo_papel_id_seq;
-- Tabelas estrangeiras (nome da referência da foreign key e nome das colunas pertinentes)
alter table projeto_pessoa
    rename column id_papel to id_tipo_papel;
alter table projeto_pessoa
    rename constraint fk_projeto_pessoa_id_papel_papel to fk_projeto_pessoa_id_tipo_papel_tipo_papel;
alter table programa_pessoa
    rename column id_papel to id_tipo_papel;
alter table programa_pessoa
    rename constraint fk_programa_pessoa_id_papel_papel to fk_programa_pessoa_id_tipo_papel_tipo_papel;

-- Tabela "status"
-- Nome da Tabela
alter table status
    rename to tipo_status;
-- Nome da constraint de primary key
alter table tipo_status
    rename constraint pk_status to pk_tipo_status;
-- Renomeia coluna "status" para "tipo"
alter table tipo_status
    rename column status to tipo;
-- Tabelas estrangeiras (nome da referência da foreign key e nome das colunas pertinentes)
alter table organizacao
    rename column status to id_tipo_status;
alter table organizacao
    rename constraint fk_organizacao_status_status to fk_organizacao_id_tipo_status_tipo_status;
alter table projeto
    rename column status to id_tipo_status;
alter table projeto
    rename constraint fk_projeto_status_status to fk_projeto_id_tipo_status_tipo_status;
alter table projeto_pessoa
    rename column id_status to id_tipo_status;
alter table projeto_pessoa
    rename constraint fk_projeto_pessoa_id_status_status to fk_projeto_pessoa_id_tipo_status_tipo_status;
alter table programa
    rename column id_status to id_tipo_status;
alter table programa
    rename constraint fk_programa_status_status to fk_programa_id_tipo_status_tipo_status;
alter table programa_pessoa
    rename constraint fk_programa_pessoa_id_status_status to fk_programa_pessoa_id_tipo_status_tipo_status;

-- Tabela "valor"
-- Nome da Tabela
alter table valor
    rename to tipo_valor;
-- Nome da constraint de primary key
alter table tipo_valor
    rename constraint valor_pkey to pk_tipo_valor;
-- Tabelas estrangeiras (nome da referência da foreign key e nome das colunas pertinentes)
alter table projeto_valor
    rename column id_valor to id_tipo_valor;
alter table projeto_valor
    rename constraint fk_projeto_valor_id_valor_valor to fk_projeto_valor_id_tipo_valor_tipo_valor;
alter table programa_valor
    rename column id_valor to id_tipo_valor;
alter table programa_valor
    rename constraint fk_programa_valor_id_valor_valor to fk_programa_valor_id_tipo_valor_tipo_valor;
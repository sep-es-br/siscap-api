create table papel_projeto
(
    id   uuid
        constraint pk_papel_projeto primary key,
    nome varchar not null
);

alter table projeto_pessoa
    add column id_papel_projeto uuid;
alter table projeto_pessoa
    add constraint fk_projeto_pessoa_id_papel_projeto_papel_projeto
        foreign key (id_papel_projeto) references papel_projeto (id);

INSERT INTO papel_projeto (id, nome)
VALUES ('01900341-a1dd-7f43-bd07-7950f4344e83', 'Parte Interessada');
INSERT INTO papel_projeto (id, nome)
VALUES ('01900341-7d72-7cce-9c0f-2d6e38873ce6', 'Membro do Projeto');
INSERT INTO papel_projeto (id, nome)
VALUES ('01900341-5f0c-7864-9470-42b1fec27249', 'Patrocinador');
INSERT INTO papel_projeto (id, nome)
VALUES ('0190035f-2ae8-7cf5-bb2d-dd232a45d1f5', 'Gerente do Projeto');

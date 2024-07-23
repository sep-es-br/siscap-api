alter table projeto_pessoa add column justificativa varchar(255);

update projeto_pessoa set justificativa = null;
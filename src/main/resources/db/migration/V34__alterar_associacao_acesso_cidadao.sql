alter table pessoa add column sub_novo varchar unique;

alter table usuario drop column email;
alter table usuario add unique (sub_novo);
-- criando campo novo no projeto para indentificar qual o ppa-loa linkado ao dic na sua criação;
ALTER TABLE projeto
    ADD COLUMN periodo_ppa_loa varchar(20);

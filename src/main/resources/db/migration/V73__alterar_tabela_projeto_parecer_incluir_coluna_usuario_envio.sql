-- 22/10/2025
-- Criacao nova coluna no parecer para registrar o nome do usuario que fez o 
-- envio do parcer para o E-Docs
alter table projeto_parecer
    add column sub_usuario_enviou varchar
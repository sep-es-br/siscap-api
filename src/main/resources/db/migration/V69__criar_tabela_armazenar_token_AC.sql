
-- 26/09/2025
-- Criacao de uma tabela para armazenar TOKEN puro do AC para
-- poder utiliza-lo no consumo da API do E-Docs

CREATE TABLE tokens_ac (
    sub_usuario VARCHAR(255) PRIMARY KEY, -- o "sub" do JWT do AC
    token TEXT NOT NULL,                  -- o token puro
    data_expiracao TIMESTAMP NOT NULL     -- quando o token expira
);
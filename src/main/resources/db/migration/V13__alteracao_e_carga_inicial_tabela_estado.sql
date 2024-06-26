alter table estado drop column if exists id_regiao_ibge;
alter table estado drop column if exists nome_regiao;
alter table estado drop column if exists sigla_regiao;
alter table estado alter column apagado set default FALSE;

INSERT INTO pais (id, nome, continente, subcontinente, iso_alpha_3, ddi, criado_em, atualizado_em, apagado) VALUES (1, 'Brasil', 'America', 'America do Sul', 'BRA', '+55', '2024-02-08 15:12:27.000000', null, false);

INSERT INTO estado (id, id_ibge, nome, id_pais, sigla) VALUES ('1', '11', 'Rondônia', '1', 'RO');
INSERT INTO estado (id, id_ibge, nome, id_pais, sigla) VALUES ('2', '12', 'Acre', '1', 'AC');
INSERT INTO estado (id, id_ibge, nome, id_pais, sigla) VALUES ('3', '13', 'Amazonas', '1', 'AM');
INSERT INTO estado (id, id_ibge, nome, id_pais, sigla) VALUES ('4', '14', 'Roraima', '1', 'RR');
INSERT INTO estado (id, id_ibge, nome, id_pais, sigla) VALUES ('5', '15', 'Pará', '1', 'PA');
INSERT INTO estado (id, id_ibge, nome, id_pais, sigla) VALUES ('6', '16', 'Amapá', '1', 'AP');
INSERT INTO estado (id, id_ibge, nome, id_pais, sigla) VALUES ('7', '17', 'Tocantins', '1', 'TO');
INSERT INTO estado (id, id_ibge, nome, id_pais, sigla) VALUES ('8', '21', 'Maranhão', '1', 'MA');
INSERT INTO estado (id, id_ibge, nome, id_pais, sigla) VALUES ('9', '22', 'Piauí', '1', 'PI');
INSERT INTO estado (id, id_ibge, nome, id_pais, sigla) VALUES ('10', '23', 'Ceará', '1', 'CE');
INSERT INTO estado (id, id_ibge, nome, id_pais, sigla) VALUES ('11', '24', 'Rio Grande do Norte', '1', 'RN');
INSERT INTO estado (id, id_ibge, nome, id_pais, sigla) VALUES ('12', '25', 'Paraíba', '1', 'PB');
INSERT INTO estado (id, id_ibge, nome, id_pais, sigla) VALUES ('13', '26', 'Pernambuco', '1', 'PE');
INSERT INTO estado (id, id_ibge, nome, id_pais, sigla) VALUES ('14', '27', 'Alagoas', '1', 'AL');
INSERT INTO estado (id, id_ibge, nome, id_pais, sigla) VALUES ('15', '28', 'Sergipe', '1', 'SE');
INSERT INTO estado (id, id_ibge, nome, id_pais, sigla) VALUES ('16', '29', 'Bahia', '1', 'BA');
INSERT INTO estado (id, id_ibge, nome, id_pais, sigla) VALUES ('17', '31', 'Minas Gerais', '1', 'MG');
INSERT INTO estado (id, id_ibge, nome, id_pais, sigla) VALUES ('18', '32', 'Espírito Santo', '1', 'ES');
INSERT INTO estado (id, id_ibge, nome, id_pais, sigla) VALUES ('19', '33', 'Rio de Janeiro', '1', 'RJ');
INSERT INTO estado (id, id_ibge, nome, id_pais, sigla) VALUES ('20', '35', 'São Paulo', '1', 'SP');
INSERT INTO estado (id, id_ibge, nome, id_pais, sigla) VALUES ('21', '41', 'Paraná', '1', 'PR');
INSERT INTO estado (id, id_ibge, nome, id_pais, sigla) VALUES ('22', '42', 'Santa Catarina', '1', 'SC');
INSERT INTO estado (id, id_ibge, nome, id_pais, sigla) VALUES ('23', '43', 'Rio Grande do Sul', '1', 'RS');
INSERT INTO estado (id, id_ibge, nome, id_pais, sigla) VALUES ('24', '50', 'Mato Grosso do Sul', '1', 'MS');
INSERT INTO estado (id, id_ibge, nome, id_pais, sigla) VALUES ('25', '51', 'Mato Grosso', '1', 'MT');
INSERT INTO estado (id, id_ibge, nome, id_pais, sigla) VALUES ('26', '52', 'Goiás', '1', 'GO');
INSERT INTO estado (id, id_ibge, nome, id_pais, sigla) VALUES ('27', '53', 'Distrito Federal', '1', 'DF');

# SisCap — Sistema de Captação de Recursos

O SisCap apoia a organização e o acompanhamento de propostas para captação de recursos do Governo do Estado do Espírito Santo. O sistema reúne informações dos projetos, seu planejamento e os pareceres necessários à análise das propostas.

Este repositório contém a **API do SisCap**, utilizada pelo frontend para consultar e registrar os dados e executar os fluxos do sistema.

## Tecnologias

<p>
  <img src="https://img.shields.io/badge/Java-17-ED8B00?style=flat" alt="Java 17" height="20" align="middle" />
  &nbsp;
  <img src="https://img.shields.io/badge/Spring_Boot-3.2.2-6DB33F?style=flat&amp;logo=springboot&amp;logoColor=white" alt="Spring Boot 3.2.2" height="20" align="middle" />
  &nbsp;
  <img src="https://img.shields.io/badge/PostgreSQL-4169E1?style=flat&amp;logo=postgresql&amp;logoColor=white" alt="PostgreSQL" height="20" align="middle" />
  &nbsp;
  <img src="https://img.shields.io/badge/Maven-3.9.5-C71A36?style=flat&amp;logo=apachemaven&amp;logoColor=white" alt="Maven 3.9.5" height="20" align="middle" />
</p>

- **Flyway:** versionamento e aplicação das migrações do banco de dados.
- **Spring Security e OAuth2:** autenticação e controle de acesso.
- **OpenFeign:** comunicação com APIs externas.
- **JasperReports:** geração de relatórios e documentos PDF.

## O que o sistema reúne

- **Projetos e DIC:** elaboração e acompanhamento do Documento Inicial de Captação, com responsáveis, equipe, objetivos e informações da proposta.
- **Indicadores e ODS:** associação de indicadores, metas e Objetivos de Desenvolvimento Sustentável aos projetos.
- **Ações e planejamento:** registro das ações, valores, distribuição por localidades e relação com o planejamento PPA/LOA.
- **Pareceres:** análise estratégica, orçamentária e de captação das propostas.
- **Programas e prospecções:** organização de iniciativas e informações relacionadas à captação de recursos.
- **Pessoas e organizações:** gestão dos participantes, proponentes e instituições envolvidas.

## Integrações

A API se integra ao **Acesso Cidadão** para autenticação e consulta de informações de usuários, ao **Organograma** para informações institucionais, ao **E-Docs** para procedimentos documentais e ao **BI** para consultas a catálogos e dados de planejamento.

## Documentação

Para preparar o ambiente, configurar o banco e executar o backend, consulte:

**[Guia para rodar a API](doc/configuracao-api.md)**

O guia inclui os pré-requisitos, as configurações locais, os comandos para Windows e Linux/macOS, a execução de testes, o acesso ao Swagger e a solução de problemas frequentes.

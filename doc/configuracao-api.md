# Guia para rodar a API do SisCap

Backend do SisCap, responsável pelos projetos (DIC), programas, pareceres, organizações, pessoas e integrações com Acesso Cidadão, Organograma, E-Docs e BI.

## Tecnologias e pré-requisitos

- JDK 17, com `JAVA_HOME` apontando para o JDK.
- Spring Boot 3.2.2.
- Maven Wrapper incluído no repositório: não é necessário instalar Maven separadamente. O wrapper utiliza Maven 3.9.5 e precisa de acesso aos repositórios de dependências no primeiro uso.
- PostgreSQL acessível pela aplicação. O Compose do projeto utiliza PostgreSQL 12.17.
- Docker com Compose, caso opte pelo banco local em contêiner.
- Configurações e credenciais de desenvolvimento fornecidas pela equipe. O funcionamento das integrações pode exigir acesso à rede institucional/VPN.

Execute os comandos deste documento na raiz do backend, onde estão `pom.xml`, `mvnw` e `mvnw.cmd`.

## Antes de iniciar: configurações essenciais

Prepare estes itens antes de tentar executar a API:

1. **Configuração local:** obtenha com a equipe o `src/main/resources/application.yml` do ambiente de desenvolvimento. Ele não está versionado. Confira também o perfil ativo.
2. **Pastas de arquivos:** crie a estrutura de documentos, cartas-consulta e imagens e confira os caminhos no YAML.
3. **Permissões:** obtenha o `authorities-mapping.json` válido e configure `authorities.file`. Sem esse arquivo, a API não inicializa.
4. **Banco:** configure PostgreSQL e Flyway para a mesma base de desenvolvimento.
5. **Integrações:** confira as credenciais, os endereços e o acesso à rede institucional/VPN necessários ao ambiente.

A estrutura de pastas e a correção do erro de permissões estão abaixo. As instruções detalhadas de banco e execução aparecem nas próximas seções.

### Pastas locais e arquivo de permissões

Antes de executar a API, prepare esta estrutura na raiz do projeto backend:

```text
siscap-api/
├── pom.xml
├── src/
└── siscap/
    └── api/
        ├── authorities-mapping.json
        └── arquivos/
            ├── documentos/
            │   └── carta-consulta/
            └── imagens/
```

- `documentos`: armazenamento dos documentos da aplicação.
- `documentos/carta-consulta`: armazenamento dos arquivos de texto das cartas-consulta. Essa subpasta é derivada de `raiz.documentos` pelo serviço, sem uma propriedade separada.
- `imagens`: armazenamento das imagens de perfil.
- `authorities-mapping.json`: **arquivo JSON**, não uma pasta. Contém o mapeamento de papéis para permissões e deve ser obtido com a equipe e copiado para o local indicado. Criar apenas um arquivo vazio não é suficiente: a API precisa carregar um JSON válido e com o mapeamento do ambiente na inicialização.

Na raiz do backend, crie as pastas:

PowerShell:

```powershell
New-Item -ItemType Directory -Force -Path './siscap/api/arquivos/documentos/carta-consulta', './siscap/api/arquivos/imagens' | Out-Null
```

Linux/macOS:

```sh
mkdir -p siscap/api/arquivos/documentos/carta-consulta siscap/api/arquivos/imagens
```

Depois de copiar o arquivo de permissões fornecido pela equipe, configure os caminhos no `application.yml`:

```yaml
raiz:
  documentos: ./siscap/api/arquivos/documentos
  imagens: ./siscap/api/arquivos/imagens
  relatorios: jasper

authorities:
  file: ./siscap/api/authorities-mapping.json
```

Incorpore as propriedades aos blocos existentes, sem duplicar chaves. Os caminhos relativos são resolvidos a partir do **diretório de trabalho do processo**: execute o Maven/JAR na raiz do backend e use esse mesmo diretório na configuração da IDE.

Se o ambiente utilizar a estrutura fora do repositório, por exemplo `C:/siscap/api`, crie as mesmas pastas nessa localização e utilize os caminhos absolutos correspondentes no YAML: `C:/siscap/api/arquivos/documentos`, `C:/siscap/api/arquivos/imagens` e `C:/siscap/api/authorities-mapping.json`. Os caminhos precisam coincidir com o local onde as pastas e o arquivo realmente estão.

O usuário que executa a API precisa ter permissão de leitura no JSON e de leitura e escrita nas pastas de arquivos. O armazenamento de anexos de parecer utiliza ainda `api.parecer.anexos_pdf`: prepare também o diretório definido nessa propriedade quando utilizar essa funcionalidade.

### Arquivo de permissões não encontrado

Se a estrutura local ou o caminho `authorities.file` não estiverem configurados, a inicialização pode falhar com:

```text
Caused by: java.io.FileNotFoundException: C:\siscap\api\authorities-mapping.json (O sistema não pode encontrar o caminho especificado)
```

Para corrigir:

1. Confira o valor de `authorities.file` no `application.yml` e no perfil ativo.
2. Crie a pasta indicada no caminho, por exemplo `C:/siscap/api`.
3. Solicite à equipe o arquivo `authorities-mapping.json` válido e coloque-o nessa pasta. Não basta criar uma pasta com esse nome ou um arquivo vazio.
4. Se o JSON estiver em outro local, ajuste `authorities.file` para o caminho real. Para caminhos relativos, confira também o diretório de trabalho da IDE ou do terminal.
5. Verifique a permissão de leitura do arquivo e reinicie a API.

Exemplo para o caminho apresentado no erro:

```yaml
authorities:
  file: C:/siscap/api/authorities-mapping.json
```

## 1. Preparar as configurações

O arquivo `src/main/resources/application.yml` está no `.gitignore` e **não acompanha um clone novo**. Solicite à equipe uma configuração de desenvolvimento válida antes de iniciar a API.

Os arquivos `application-dev.yml`, `application-hml.yml` e `application-prod.yml` também estão ignorados. Não publique credenciais no README nem versione esses arquivos.

Confira estas propriedades na configuração recebida:

| Configuração | Finalidade |
| --- | --- |
| `spring.datasource.url`, `username`, `password` | Conexão da aplicação com PostgreSQL. |
| `spring.flyway.url`, `user`, `password` | Conexão utilizada pelas migrações. Deve apontar para a mesma base da aplicação. |
| `spring.flyway.enabled` | Habilita as migrações; a configuração local consultada utiliza `true`. |
| `spring.jpa.hibernate.ddl-auto` | A configuração consultada utiliza `none`; a estrutura é mantida pelo Flyway. |
| `server.port`, `server.servlet.context-path` | Porta e prefixo da API; a configuração local consultada utiliza `8080` e `/siscap`. |
| `spring.security.oauth2.client.*`, `spring.security.oauth2.resourceserver.*` | Registro OAuth2, issuer e validação da autenticação. |
| `api.acessocidadao.*`, `api.organograma.*` | Endereços e credenciais das integrações. |
| `api.edocs.*`, `api.parecer.*`, `api.programa.*` | E-Docs, unidades, pareceres e assinantes do ambiente. |
| `token.secret`, `oauth2.redirect-state-secret` | Segredos utilizados pela aplicação. |
| `frontend.host`, `oauth2.redirect.frontend.*` | Endereços do frontend e dos retornos de autenticação. |
| `spring.security.oauth2.client.registration.acessocidadao.redirect-uri` | Callback da API, compatível com o endereço cadastrado no provedor OAuth2. |
| `authorities.file` | Caminho de um JSON válido com o mapeamento de papéis e permissões. Solicite o arquivo à equipe; ele é lido na inicialização. |
| `raiz.imagens`, `raiz.documentos`, `api.parecer.anexos_pdf` | Diretórios locais de armazenamento, com permissão de leitura e escrita para o processo da API. |
| `raiz.relatorios` | Localização dos relatórios Jasper; a configuração consultada utiliza `jasper`. |
| `spring.mail.*`, `email.*` | SMTP, remetentes e destinatários do ambiente de desenvolvimento. |
| `pentahoBI.*` | Acesso ao BI usado pelos catálogos e pelo planejamento. |

O YAML precisa conter as demais propriedades exigidas pelos serviços, além das listadas acima. Alterar apenas a conexão do banco não substitui a configuração completa da aplicação.

Para o frontend local, confira `frontend.host: http://localhost:4200` e os retornos correspondentes. Ajuste os diretórios para seu sistema operacional; não reutilize caminhos Windows em Linux.

## 2. Preparar o banco de dados

### Opção A: PostgreSQL com Docker Compose

```sh
docker compose up -d postgres_siscap
docker compose ps
docker compose logs postgres_siscap
```

O serviço publica a porta `5432`, cria o contêiner `pg_siscap` e usa um volume persistente. O script `dataset/init.sql` cria a base `siscap` na primeira inicialização de um volume vazio.

Configure o YAML local para esse banco:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/siscap
    username: postgres
    password: ${SISCAP_DB_PASSWORD}
  flyway:
    url: jdbc:postgresql://localhost:5432/siscap
    user: postgres
    password: ${SISCAP_DB_PASSWORD}
    enabled: true
```

Este trecho deve ser incorporado ao bloco `spring` do YAML completo, sem duplicar a chave. Defina `SISCAP_DB_PASSWORD` com o mesmo valor de `POSTGRES_PASSWORD` do `docker-compose.yml`. A variável usada neste exemplo configura a API; ela não altera a senha do contêiner existente.

PowerShell:

```powershell
$env:SISCAP_DB_PASSWORD = '<senha do banco local>'
```

Linux/macOS:

```sh
export SISCAP_DB_PASSWORD='<senha do banco local>'
```

Para parar o banco sem apagar os dados:

```sh
docker compose stop postgres_siscap
```

### Opção B: PostgreSQL existente

Crie ou solicite uma base de desenvolvimento chamada `siscap`. Configure tanto o datasource quanto o Flyway com host, porta, usuário e senha dessa base. O usuário das migrações precisa conseguir criar e alterar os objetos do banco.

As migrações ficam em `src/main/resources/db/migration` e são executadas pelo Flyway durante a inicialização. Não é necessário executar os scripts SQL individualmente.

## 3. Executar a API

### Windows / PowerShell

```powershell
java -version
.\mvnw.cmd -version
.\mvnw.cmd spring-boot:run
```

### Linux/macOS

```sh
java -version
chmod +x mvnw
./mvnw -version
./mvnw spring-boot:run
```

Para selecionar o perfil `dev`, caso a equipe tenha fornecido sua configuração:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
```

```sh
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Sem selecionar um perfil, o Spring Boot utiliza a configuração padrão. Não há um perfil de desenvolvimento completo versionado neste repositório.

Na IDE, selecione JDK 17, importe o projeto Maven e execute `br.gov.es.siscap.SiscapApplication`, com a raiz do backend como diretório de trabalho. Habilite o processamento de anotações do Lombok caso a IDE indique erros em código gerado.

## 4. Conferir a inicialização

Com a configuração local na porta `8080` e contexto `/siscap`:

- API: `http://localhost:8080/siscap`.
- Versão: `http://localhost:8080/siscap/versions`.
- Swagger UI: `http://localhost:8080/siscap/swagger-ui/index.html`.
- OpenAPI: `http://localhost:8080/siscap/v3/api-docs`.

PowerShell:

```powershell
Invoke-RestMethod http://localhost:8080/siscap/versions
```

Linux/macOS:

```sh
curl http://localhost:8080/siscap/versions
```

O endpoint de versão é público e retorna JSON com `success` e `data.version`. Ele confirma que a API responde, mas não verifica todas as integrações. As rotas de negócio dependem da autenticação e das permissões do usuário; uma resposta `401` ou `403` nessas rotas não significa necessariamente falha na inicialização.

Configure a URL base do frontend como `http://localhost:8080/siscap` para consumir esta instância.

## Testes e empacotamento

| Operação | PowerShell | Linux/macOS |
| --- | --- | --- |
| Executar testes | `.\mvnw.cmd test` | `./mvnw test` |
| Gerar JAR com testes | `.\mvnw.cmd clean package` | `./mvnw clean package` |
| Gerar JAR sem executar testes | `.\mvnw.cmd clean package -DskipTests` | `./mvnw clean package -DskipTests` |

Para executar o JAR gerado:

```sh
java -jar target/siscap-0.0.1-SNAPSHOT.jar
```

Com perfil:

```sh
java -jar target/siscap-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

O `docker-compose.yml` incluído sobe somente o PostgreSQL. O `Dockerfile` empacota a API, mas uma execução em contêiner também precisa receber a configuração do ambiente e acesso aos serviços externos. O roteiro local acima executa a API pelo Maven ou pela IDE.

## Problemas frequentes

| Sintoma | O que conferir |
| --- | --- |
| Java incompatível ou erro de compilação | `java -version`, `JAVA_HOME` e o JDK mostrado por `mvnw -version`; utilize JDK 17. |
| Falha no download de dependências | Rede, proxy e acesso ao Maven Central. |
| `Could not resolve placeholder` | Propriedade ausente no YAML ou perfil selecionado sem a configuração correspondente. |
| Falha de conexão/autenticação no PostgreSQL | Banco em execução, porta, usuário, senha e URLs do datasource e do Flyway. |
| Base `siscap` não encontrada | O `init.sql` só roda automaticamente no primeiro uso de um volume vazio; confira se a base foi criada. |
| Erro do Flyway | Consulte a migração e o erro original no log; confira a base e suas permissões antes de alterar histórico ou scripts. |
| JSON de permissões não encontrado ou inválido | Caminho `authorities.file`, existência, conteúdo e permissão de leitura do arquivo fornecido pela equipe. |
| Erro ao acessar imagens, documentos ou anexos | Caminhos configurados e permissão de escrita nos diretórios. |
| Porta `8080` ou `5432` ocupada | Verifique instâncias já iniciadas ou ajuste a porta e as URLs correspondentes. |
| Login, E-Docs, catálogos ou BI indisponíveis | Rede institucional/VPN, endereços e credenciais do ambiente; confira o callback OAuth2 e as permissões. |

Os logs são enviados ao console e, pela configuração `log4j2-spring.xml`, ao arquivo `logs/siscap-api.log`, relativo ao diretório de trabalho. Use o erro original do backend para diagnosticar uma resposta HTTP 400/500.

[Voltar à apresentação do SisCap](../README.md)

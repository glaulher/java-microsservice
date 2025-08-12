# Projeto de Estudo: Microserviços com Spring Boot e Spring Cloud

Este projeto é um ambiente de estudo para a construção de uma arquitetura de microsserviços utilizando o ecossistema Spring.

Atualmente, a estrutura é composta por um serviço central que desempenha os papéis de **Config Server** e **Eureka Server (Service Discovery)**, além de um repositório de configuração versionado com Git.

## Estrutura do Projeto

- **/service-main**: Aplicação Spring Boot que atua como o coração da infraestrutura dos microsserviços.
  - **Spring Cloud Config Server**: Centraliza a configuração de todas as aplicações.
  - **Spring Cloud Netflix Eureka**: Atua como um servidor de registro e descoberta de serviços, permitindo que os microsserviços se encontrem dinamicamente na rede.
- **/config-server**: Um diretório que espelha a estrutura de um repositório Git contendo os arquivos de configuração para os microsserviços.

---

## Configuração Detalhada

A seguir, uma explicação sobre os principais arquivos de configuração do projeto.

### 1. Service Main (`service-main`)

Este é o primeiro serviço a ser executado, pois ele fornecerá a configuração e o registro para todos os outros.

#### `pom.xml`

As dependências-chave que habilitam as funcionalidades deste serviço são:

- `spring-cloud-config-server`: Transforma a aplicação em um **Config Server**.
- `spring-cloud-starter-netflix-eureka-server`: Transforma a aplicação em um **Eureka Server**.

#### `src/main/resources/application.properties`

```properties
# Nome da aplicação no Spring
spring.application.name=service-main

# Porta em que o servidor principal (Config/Eureka) irá rodar
server.port=8888

# --- Configuração do Spring Cloud Config Server ---

# URI do repositório Git de onde as configurações serão lidas
spring.cloud.config.server.git.uri=https://github.com/glaulher/config-server.git

# Força o servidor a clonar o repositório na inicialização
spring.cloud.config.server.git.clone-on-start=true

# Subdiretório dentro do repositório onde os arquivos de configuração estão localizados
spring.cloud.config.server.git.search-paths=config

# --- Configuração do Eureka Server ---

# Desabilita o registro do próprio servidor Eureka com ele mesmo
eureka.client.registerWithEureka=false

# Desabilita a busca por outros registros de servidores Eureka
eureka.client.fetchRegistry=false

# Hostname para o servidor Eureka
eureka.instance.hostname=localhost

# Desabilita o modo de auto-preservação (útil em ambientes de desenvolvimento)
eureka.server.enableSelfPreservation=false
```

### 2. Repositório de Configuração (`config-server`)

Este diretório contém os arquivos `.properties` que serão fornecidos aos microsserviços pelo **Config Server**. O nome do arquivo corresponde ao `spring.application.name` do microsserviço cliente.

#### `service-one.properties`

Este arquivo contém a configuração para um futuro microsserviço chamado `service-one`.

```properties
# Uma propriedade customizada que será injetada no microsserviço
message=Hello from GitHub Config Service

# Define a porta em que o "service-one" deverá rodar
server.port=8081
```


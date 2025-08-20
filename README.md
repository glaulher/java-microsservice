# Projeto de Estudo: Microserviços com Spring Boot e Spring Cloud

Este projeto é um ambiente de estudo para a construção de uma arquitetura de microsserviços utilizando o ecossistema Spring.

Atualmente, a estrutura é composta por um serviço central que desempenha os papéis de **Config Server** e **Eureka Server (Service Discovery)**, um microsserviço de exemplo (`service-tasks`) e um repositório de configuração versionado com Git.

## Estrutura do Projeto

- **/service-main**: Aplicação Spring Boot que atua como o coração da infraestrutura dos microsserviços.
  - **Spring Cloud Config Server**: Centraliza a configuração de todas as aplicações.
  - **Spring Cloud Netflix Eureka**: Atua como um servidor de registro e descoberta de serviços, permitindo que os microsserviços se encontrem dinamicamente na rede.
- **/service-tasks**: Um microsserviço Spring Boot que consome a configuração do Config Server e se registra no Eureka Server.
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
- `org.projectlombok:lombok`: (Adicionado) Utilitário para reduzir código boilerplate.

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

# (Adicionado) Prefixo para os endpoints do Config Server
spring.cloud.config.server.prefix=/config

# --- Configuração do Eureka Server ---

# Desabilita o registro do próprio servidor Eureka com ele mesmo
eureka.client.registerWithEureka=false

# Desabilita a busca por outros registros de servidores Eureka
eureka.client.fetchRegistry=false

# Hostname para o servidor Eureka
eureka.instance.hostname=localhost
```

### 2. Service Tasks (`service-tasks`)

Este é um microsserviço que demonstra como um cliente pode interagir com o Config Server e o Eureka Server, além de expor uma API REST para gerenciar tarefas.

#### `pom.xml`

As dependências-chave que habilitam as funcionalidades deste serviço são:

- `spring-boot-starter-web`: Para criar endpoints REST.
- `spring-boot-starter-data-jpa`: Para interagir com o banco de dados.
- `spring-cloud-starter-config`: Para se conectar ao **Config Server**.
- `spring-cloud-starter-netflix-eureka-client`: Para se registrar no **Eureka Server**.
- `org.projectlombok:lombok`: Utilitário para reduzir código boilerplate.

#### `src/main/java/com/glaulher/service_tasks/TasksEntity.java`

Esta é a entidade que representa uma tarefa no banco de dados. A anotação `@Data` do Lombok é usada para gerar automaticamente getters, setters, `toString`, `equals` e `hashCode`.

```java
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TasksEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;
  private LocalDateTime dueDate;
  private boolean notified;
}
```

#### `src/main/java/com/glaulher/service_tasks/TasksRepository.java`

Este é o repositório que gerencia as operações de banco de dados para a `TasksEntity`.

```java
public interface TasksRepository extends JpaRepository<TasksEntity, Long> {}
```

#### `src/main/java/com/glaulher/service_tasks/TasksController.java`

Este controller expõe um endpoint `/tasks` que permite a criação de novas tarefas.

```java
@RestController
@RequestMapping("/tasks")
public class TasksController {

  public final TasksRepository tasksRepository;

  public TasksController(TasksRepository tasksRepository) {
    this.tasksRepository = tasksRepository;
  }

  @PostMapping
  ResponseEntity<TasksEntity> createTask(@RequestBody TasksEntity task) {
    return ResponseEntity.ok(tasksRepository.save(task));
  }
}
```

### 3. Repositório de Configuração (`config-server`)

Este diretório contém os arquivos `.properties` que serão fornecidos aos microsserviços pelo **Config Server**.

#### `service-tasks.properties`

Este arquivo contém a configuração para o microsserviço `service-tasks`.

```properties
# Uma propriedade customizada que será injetada no microsserviço
message=Hello from GitHub Config Service

# Define a porta em que o "service-tasks" deverá rodar
server.port=8081
```

---

## Aprendizados até aqui

- **Config Server**: Foi configurado um servidor de configuração centralizado usando o Spring Cloud Config. Ele serve as configurações de um repositório Git, permitindo que os microsserviços obtenham suas configurações de uma fonte centralizada e versionada.
- **Eureka Server**: Foi configurado um servidor de descoberta de serviços usando o Spring Cloud Netflix Eureka. Isso permite que os microsserviços se registrem e descubram uns aos outros dinamicamente na rede.
- **Cliente de Configuração e Eureka**: O `service-tasks` demonstra como um microsserviço pode atuar como um cliente tanto do Config Server quanto do Eureka Server. Ele busca suas configurações no Config Server e se registra no Eureka, tornando-se detectável por outros serviços.
- **Injeção de Propriedades**: Foi utilizado a anotação `@Value` para injetar propriedades de configuração do Config Server diretamente em um bean do Spring.
- **API REST com Spring Data JPA**: Foi criada uma API REST para gerenciar tarefas, utilizando o Spring Data JPA para simplificar a persistência de dados.
- **Lombok**: O Lombok foi utilizado para reduzir a quantidade de código boilerplate nas entidades, gerando automaticamente getters, setters e outros métodos.
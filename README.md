# Sistema de Recomendação de Livros e Filmes  

Este é um sistema de recomendação que sugere livros e filmes similares ao conteúdo no histórico do usuário, e obras bem avaliadas por outros usuários que consomem obras semelhantes. 

## Funcionalidades

### Essenciais
- (Base de dados) Criar as tables Obra, Usuario, Avaliacao
- (Java & API) Cadastro e login de usuários
- (Java & API) Avaliação de obras
- (Java & API) Recomendações personalizadas
- (Interface) Página para cadastro de usuário
- (Interface) Página para exibição do catálogo
- (Interface) Página para exibição das recomendações
- (Interface) Opção para avaliar obra

### Importantes
- (Banco de dados) Obter dados de livros e filmes em grande quantidade para o banco de dados
- (Banco de dados) Salvar a descrição e um link para a imagem de capa da obra no banco
- (Java & API) Funções de pesquisa no banco de dados
- (Interface) Exibir as capas das obras na interface
- (Interface) Página pra exibir detalhes (descrição, autor, etc.) de uma obra
- (Interface) Função de pesquisa por título

### Desejáveis
- (Banco de dados) Se for possível, Obter dados de usuários de outros sites para formar uma base para a filtragem colaborativa
- (Interface) Função de pesquisa avançada (Por autor, com filtro pra gêneros, etc.)
- (Interface & Java & API) Sistema de notificações para novas recomendações
- (Interface & Java & API) Opção de blacklist e whitelist para recomendações

## Tecnolgias Utilizadas:
- Java 21
- Spring Boot
- Maven
- MySQL
- Docker Desktop

### Spring Initializr:
- Project: Maven
- Language: Java
- Spring Boot: 3.4.4
- Packaging: Jar
- Java: 21

#### Dependências:
- Spring Web
- Spring Boot DevTools
- Spring Data JPA
- MySQL Driver
- Spring Boot Validation
- Spring Security
- Lombok
- Docker Compose Support
- Testcontainers
# Sistema de Recomendação de Livros

Este projeto é um sistema de recomendação que sugere livros similares aos livros no histórico do usuário, e livros bem avaliados por outros usuários que leem livros semelhantes. 

## Tecnolgias Utilizadas:
- Java 21
- Spring Boot
- Maven
- MySQL
- Docker Desktop
- Insomnia

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

## Algoritmo de Recomendação

O K-Nearest Neighbors (KNN), ou K vizinhos mais próximos, é um método de aprendizado preguiçoso e não
paramétrico que classifica ou prevê valores com base na proximidade de instâncias no
espaço de características. Cada ponto de dados — no nosso caso, um livro
representado por um vetor de metadados (título, descrição, gênero, autor, ano, etc.) —
é comparado a todos os outros para determinar os K vizinhos mais próximos. A
similaridade é tipicamente medida pela distância euclidiana ou, em sistemas de
recomendação de conteúdo, pela similaridade cosseno, que avalia o ângulo entre
vetores, independentemente de sua magnitude.

As principais vantagens do KNN são a simplicidade, a explicabilidade (é fácil dizer
“recomendamos X porque é parecido com Y”) e a ausência de fase de treinamento
prévio — toda a computação ocorre no momento da consulta. Entretanto, ele sofre
com a complexidade O(n) por consulta, já que compara o perfil a cada livro, e com a
maldição da dimensionalidade em vetores muito grandes.

No contexto de recomendação de livros, usamos o KNN para sugerir novas leituras ao
usuário. Primeiro, transformamos cada livro em um vetor numérico ( **L̅** ): gêneros e autores de cada livro são salvos como HashSets (para uma busca mais rápida) de todos os IDs presentes no livro, representando valor 1, e os que não estão presentes no livro simplesmente não são salvos; atributos numéricos (ano, páginas) são
normalizados. Então, construímos o perfil do usuário ( **U̅** ) como a média ponderada
dos vetores dos livros que ele avaliou bem:

![Fórmula 1](https://latex.codecogs.com/png.image?\dpi{120}\vec{U}=\frac{1}{\sum{r_i}}\sum{r_i}\cdot\vec{L_i})

Em tempo real, calculamos a similaridade entre esse perfil e os vetores de todos os
livros não lidos (estimamos que haverá por volta de 300 mil livros no banco de dados),
ordenamos por similaridade e selecionamos os K mais próximos como
recomendações.

![Fórmula 2](https://latex.codecogs.com/png.image?\dpi{120}similaridade(\vec{U},\vec{L})=\frac{\vec{U}\cdot\vec{L}}{|\vec{U}|\cdot|\vec{L}|})

# Documentação da API

## Base URL

`http://localhost:8080/api`

---

## Sumário

- [Sistema de Recomendação de Livros](#sistema-de-recomendação-de-livros)
	- [Tecnolgias Utilizadas:](#tecnolgias-utilizadas)
		- [Spring Initializr:](#spring-initializr)
			- [Dependências:](#dependências)
	- [Algoritmo de Recomendação](#algoritmo-de-recomendação)
- [Documentação da API](#documentação-da-api)
	- [Base URL](#base-url)
	- [Sumário](#sumário)
	- [Autenticação](#autenticação)
	- [Endpoints](#endpoints)
		- [GET /livro/](#get-livro)
		- [POST /livro/](#post-livro)
		- [GET /livro/{id}](#get-livroid)
		- [PUT /livro/{id}](#put-livroid)
		- [PATCH /livro/{id}](#patch-livroid)
		- [DELETE /livro/{id}](#delete-livroid)
		- [GET /usuario/](#get-usuario)
		- [POST /usuario/](#post-usuario)
		- [PUT /usuario/{id}](#put-usuarioid)
		- [PATCH /usuario/{id}](#patch-usuarioid)
		- [DELETE /usuario/{id}](#delete-usuarioid)
		- [GET /genero/](#get-genero)
		- [POST /genero/](#post-genero)
		- [GET /genero/{id}](#get-generoid)
		- [PUT /genero/{id}](#put-generoid)
		- [PATCH /genero/{id}](#patch-generoid)
		- [DELETE /genero/{id}](#delete-generoid)
		- [POST /genero/num-livros](#post-generonum-livros)
		- [POST /avaliacao/](#post-avaliacao)
		- [GET /avaliacao/{id}](#get-avaliacaoid)
		- [PATCH /avaliacao/{id}](#patch-avaliacaoid)
		- [DELETE /avaliacao/{id}](#delete-avaliacaoid)
		- [GET /avaliacao/livro-media](#get-avaliacaolivro-media)
		- [GET /avaliacao/livro-usuario](#get-avaliacaolivro-usuario)
		- [GET /recomendacao/conteudo/{id}](#get-recomendacaoconteudoid)
	- [Códigos de Status](#códigos-de-status)
	- [Exemplos de Erros](#exemplos-de-erros)
		- [400 Bad Request](#400-bad-request)
		- [404 Not Found](#404-not-found)

---

## Autenticação

**Ainda não implementada.**

---

## Endpoints

### GET /livro/

**Descrição:** Retorna todos os livros cadastrados.

**Requisição:**

``` bash
curl --request GET \
  --url http://localhost:8080/api/livro/ \
  --header 'User-Agent: insomnia/11.0.2'
```

**Resposta:**

```json
[
	{
		"id": 1,
		"titulo": "Fundação",
		"subtitulo": null,
		"primeira_data_publicacao": null,
		"data_publicacao": null,
		"descricao": null,
		"capa": "https://covers.openlibrary.org/b/id/10376604-L.jpg",
		"paginas": null,
		"editora": "Editora Aleph",
		"autores": [
			{
				"id": 1,
				"nome": "Isaac Asimov",
				"num_livros": null
			}
		],
		"generos": [
			{
				"id": 1,
				"nome": "Psychohistory",
				"num_livros": null
            }
		]
	},
	{
		"id": 2,
		"titulo": "Eva Luna.",
		"subtitulo": null,
		"primeira_data_publicacao": null,
		"data_publicacao": 2004,
		"descricao": null,
		"capa": null,
		"paginas": 318,
		"editora": "Difel",
		"autores": [
			{
				"id": 2,
				"nome": "Isabel Allende",
				"num_livros": null
			}
		],
		"generos": [
			{
				"id": 19,
				"nome": "Spanish language books",
				"num_livros": null
			}
		]
	}
]
```

### POST /livro/

**Descrição:** Cadastra um novo livro.

**Requisição:**
``` bash
curl --request POST \
  --url http://localhost:8080/api/livro/ \
  --header 'Content-Type: application/json' \
  --header 'User-Agent: insomnia/11.0.2' \
  --data '{"titulo": "Eva Luna.", "paginas": 318, "editora": "Difel", "data_publicacao": 2004, "autores": ["Isabel Allende"], "primeira_data_publicacao": null, "subtitulo": null, "descricao": null, "capa": null, "generos": ["Spanish language books", "Spanish fiction", "Storytellers"]}'
```

**Body (JSON):**

```json
{
    "titulo": "Divergente",
	"capa": "https://covers.openlibrary.org/b/id/12371523-L.jpg", 
	"paginas": 502, "editora": "Rocco Jovens Leitores", 
	"data_publicacao": null, 
	"autores": ["Veronica Roth"], 
	"subtitulo": null, 
	"primeira_data_publicacao": null, 
	"descricao": null, 
	"generos": ["New York Times bestseller", "nyt:paperback_books=2012-02-25", "Families", "Family", "Juvenile fiction"]
}
```

**Resposta:**

```json
{
	"id": 12,
	"titulo": "As ondas",
	"subtitulo": null,
	"primeira_data_publicacao": null,
	"data_publicacao": 1991,
	"descricao": null,
	"capa": null,
	"paginas": 222,
	"editora": "Editora Nova Fronteira",
	"autores": [
		{
			"id": 12,
			"nome": "Virginia Woolf",
			"num_livros": null
		}
	],
	"generos": [
		{
			"id": 460,
			"nome": "British and irish fiction (fictional works by one author)",
			"num_livros": null
		},
		{
			"id": 151,
			"nome": "Fiction, psychological",
			"num_livros": null
		},
		{
			"id": 408,
			"nome": "Friendship, fiction",
			"num_livros": null
		},
		{
			"id": 58,
			"nome": "Identity (Psychology)",
			"num_livros": null
		},
		{
			"id": 4,
			"nome": "Fiction",
			"num_livros": null
		},
		{
			"id": 384,
			"nome": "Facsimiles",
			"num_livros": null
		}
	]
}
```

### GET /livro/{id}

**Descrição:** Retorna um livro por id

**Requisição:**
``` bash
curl --request GET \
  --url http://localhost:8080/api/livro/3 \
  --header 'User-Agent: insomnia/11.0.2'
```

**Resposta:**
``` json
{
{
	"id": 12,
	"titulo": "As ondas",
	"subtitulo": null,
	"primeira_data_publicacao": null,
	"data_publicacao": 1991,
	"descricao": null,
	"capa": null,
	"paginas": 222,
	"editora": "Editora Nova Fronteira",
	"autores": [
		{
			"id": 12,
			"nome": "Virginia Woolf",
			"num_livros": null
		}
	],
	"generos": [
		{
			"id": 460,
			"nome": "British and irish fiction (fictional works by one author)",
			"num_livros": null
		},
		{
			"id": 151,
			"nome": "Fiction, psychological",
			"num_livros": null
		},
		{
			"id": 408,
			"nome": "Friendship, fiction",
			"num_livros": null
		},
		{
			"id": 58,
			"nome": "Identity (Psychology)",
			"num_livros": null
		},
		{
			"id": 4,
			"nome": "Fiction",
			"num_livros": null
		},
		{
			"id": 384,
			"nome": "Facsimiles",
			"num_livros": null
		}
	]
}
}
```

### PUT /livro/{id}

**Descrição:** Atualiza todos os campos de um livro (qualquer campo não presente, exceto titulo, será salvo como null, se titulo não estiver presente retorna um erro)

**Requisição:**
``` bash
curl --request PUT \
  --url http://localhost:8080/api/livro/2 \
  --header 'Content-Type: application/json' \
  --header 'User-Agent: insomnia/11.0.2' \
  --data '{"titulo": "Eva Luna.", "paginas": 318, "editora": "Difel", "data_publicacao": 2004, "autores": ["Isabel Allende"], "primeira_data_publicacao": null, "subtitulo": null, "descricao": null, "capa": null, "generos": ["Spanish language books", "Spanish fiction"]}'
```

**Body (JSON):**
``` json
{
	"titulo": "Eva Luna.", 
	"paginas": 318, 
	"editora": "Difel", 
	"data_publicacao": 2004, 
	"autores": ["Isabel Allende"], 
	"primeira_data_publicacao": null, 
	"subtitulo": null, 
	"descricao": null, 
	"capa": null, 
	"generos": ["Spanish language books", "Spanish fiction"]
}
```

**Resposta:**
``` json
{
	"id": 12,
	"titulo": "As ondas",
	"subtitulo": null,
	"primeira_data_publicacao": null,
	"data_publicacao": 1991,
	"descricao": null,
	"capa": null,
	"paginas": 222,
	"editora": "Editora Nova Fronteira",
	"autores": [
		{
			"id": 12,
			"nome": "Virginia Woolf",
			"num_livros": null
		}
	],
	"generos": [
		{
			"id": 460,
			"nome": "British and irish fiction (fictional works by one author)",
			"num_livros": null
		},
		{
			"id": 151,
			"nome": "Fiction, psychological",
			"num_livros": null
		},
		{
			"id": 408,
			"nome": "Friendship, fiction",
			"num_livros": null
		},
		{
			"id": 58,
			"nome": "Identity (Psychology)",
			"num_livros": null
		},
		{
			"id": 4,
			"nome": "Fiction",
			"num_livros": null
		},
		{
			"id": 384,
			"nome": "Facsimiles",
			"num_livros": null
		}
	]
}
```

### PATCH /livro/{id}

**Descrição:** Atualiza campos específicos do livro (e mantém os outros)

**Resquisição:**
``` bash
curl --request PATCH \
  --url http://localhost:8080/api/livro/1 \
  --header 'Content-Type: application/json' \
  --header 'User-Agent: insomnia/11.0.2' \
  --data '{"autores": ["Isaac Asimov"]}'
```

**Body (JSON, todos os campos são opcionais):**
``` json
{
	"titulo": "Eva Luna.", 
	"paginas": 318, 
	"editora": "Difel", 
	"data_publicacao": 2004, 
	"autores": ["Isabel Allende"], 
	"primeira_data_publicacao": null, 
	"subtitulo": null, 
	"descricao": null, 
	"capa": null, 
	"generos": ["Spanish language books", "Spanish fiction"]
}
```

**Resposta:**
``` json
{
	"id": 12,
	"titulo": "As ondas",
	"subtitulo": null,
	"primeira_data_publicacao": null,
	"data_publicacao": 1991,
	"descricao": null,
	"capa": null,
	"paginas": 222,
	"editora": "Editora Nova Fronteira",
	"autores": [
		{
			"id": 12,
			"nome": "Virginia Woolf",
			"num_livros": null
		}
	],
	"generos": [
		{
			"id": 460,
			"nome": "British and irish fiction (fictional works by one author)",
			"num_livros": null
		},
		{
			"id": 151,
			"nome": "Fiction, psychological",
			"num_livros": null
		},
		{
			"id": 408,
			"nome": "Friendship, fiction",
			"num_livros": null
		},
		{
			"id": 58,
			"nome": "Identity (Psychology)",
			"num_livros": null
		},
		{
			"id": 4,
			"nome": "Fiction",
			"num_livros": null
		},
		{
			"id": 384,
			"nome": "Facsimiles",
			"num_livros": null
		}
	]
}
```

### DELETE /livro/{id}

**Descrição:** Deleta um livro por id.

**Requisição:**
``` bash
curl --request DELETE \
  --url http://localhost:8080/api/livro/7 \
  --header 'User-Agent: insomnia/11.0.2'
```

### GET /usuario/

**Descrição:** Retorna todos os usuários.

**Requisição:**
``` bash
curl --request GET \
  --url http://localhost:8080/api/usuario/ \
  --header 'User-Agent: insomnia/11.0.2'
```

**Resposta:**
``` json
[
	{
		"id": 1,
		"nome": "Jonas"
	},
	{
		"id": 2,
		"nome": "Luís"
	}
]
```

### POST /usuario/

**Descrição:** Cria um novo usuário.

**Requisição:**
``` bash
curl --request POST \
  --url http://localhost:8080/api/usuario/ \
  --header 'Content-Type: application/json' \
  --header 'User-Agent: insomnia/11.0.2' \
  --data '{
	"nome": "Jonas",
	"senha": "12345"
}'
```

**Body (JSON):**
``` json
{
	"nome": "Jonas",
	"senha": "12345"
}
```

**Resposta:**
``` json
{
	"id": 1,
	"nome": "Jonas"
}
```

### PUT /usuario/{id}

**Descrição:** Atualiza todos os campos do usuario (aqueles que não estiverem presentes no JSON serão salvos com valor null)

**Requisição:**
``` bash
curl --request PUT \
  --url http://localhost:8080/api/usuario/1 \
  --header 'Content-Type: application/json' \
  --header 'User-Agent: insomnia/11.0.2' \
  --data '{
	"nome": "Mário",
	"senha": "54321"
}'
```

**Body (JSON):**
``` json
{
	"nome": "Mário",
	"senha": "54321"
}
```

**Resposta:**
``` json
{
	"id": 1,
	"nome": "Mário"
}
```

### PATCH /usuario/{id}

**Descrição:** Atualiza campos específicos de usuário (e mantém os outros).

**Requisição:**
``` bash
curl --request PATCH \
  --url http://localhost:8080/api/usuario/1 \
  --header 'Content-Type: application/json' \
  --header 'User-Agent: insomnia/11.0.2' \
  --data '{
	"nome": "Maria"
}'
```

**Body (JSON):**
``` json
{
	"nome": "Maria",
	"senha": "1020304050"
}
```

**Resposta:**
``` json
{
	"id": 1,
	"nome": "Maria"
}
```

### DELETE /usuario/{id}

**Descrição:** Deleta um usuário pelo id.

**Requisição:**
``` bash
curl --request DELETE \
  --url http://localhost:8080/api/livro/7 \
  --header 'User-Agent: insomnia/11.0.2'
```

### GET /genero/

**Descrição:** Retorna todos os gêneros.

**Requisição:**
``` bash
curl --request GET \
  --url http://localhost:8080/api/genero/ \
  --header 'User-Agent: insomnia/11.0.2'
```

**Resposta:**
``` json
[
	{
		"id": 1,
		"nome": "Psychohistory",
		"num_livros": null
	},
	{
		"id": 2,
		"nome": "Open Library Staff Picks",
		"num_livros": null
	},
	{
		"id": 3,
		"nome": "Life on other planets",
		"num_livros": null
	},
	{
		"id": 4,
		"nome": "Fiction",
		"num_livros": null
	}
]
```

### POST /genero/

**Descrição:** Cria um novo gênero.

**Requisição:**
``` bash
curl --request POST \
  --url http://localhost:8080/api/genero/ \
  --header 'Content-Type: application/json' \
  --header 'User-Agent: insomnia/11.0.2' \
  --data '{
	"nome": "Ação"
}'
```

**Body:**
``` json
{
	"nome": "Ação",
}
```

**Resposta:**
``` json
{
	"id": 1,
	"nome": "Ação",
	"num_livros": 10
}
```

### GET /genero/{id}

**Descrição:** Retorna um gênero por id.

**Requisição:**
``` bash
curl --request GET \
  --url http://localhost:8080/api/genero/12 \
  --header 'User-Agent: insomnia/11.0.2'
```

**Resposta:**
``` json
{
	"id": 12,
	"nome": "Ação",
	"num_livros": 20
}
```

### PUT /genero/{id}

**Descrição:** Atualiza todos os campos de gênero (aqueles que não estiverem no JSON serão salvos como null)

**Requisição:**
``` bash
curl --request PUT \
  --url http://localhost:8080/api/genero/12 \
  --header 'Content-Type: application/json' \
  --header 'User-Agent: insomnia/11.0.2' \
  --data '{
	"nome": "Acao"
}'
```

**Body:**
``` json
{
	"nome": "Acao"
}
```

**Resposta:**
``` json
{
	"id": 12,
	"nome": "Acao",
	"num_livros": null
}
```

### PATCH /genero/{id}

**Descrição:** Atualiza campos específicos de gênero (e mantém os outros).

**Requisição:**
``` bash
curl --request PATCH \
  --url http://localhost:8080/api/genero/12 \
  --header 'Content-Type: application/json' \
  --header 'User-Agent: insomnia/11.0.2' \
  --data '{
	"nome": "Acao"
}'
```

**Body:**
``` json
{
	"nome": "Acao"
}
```

**Resposta:**
``` json
{
	"id": 12,
	"nome": "Acao",
	"num_livros": 20
}
```

### DELETE /genero/{id}

**Descrição:** Deleta um gênero por id.

**Requisição:**
``` bash
curl --request DELETE \
  --url http://localhost:8080/api/genero/12 \
  --header 'User-Agent: insomnia/11.0.2'
```

### POST /genero/num-livros

**Descrição:** Atualiza o campo num_livros para todos os gêneros.

**Requisição:**
``` bash
curl --request POST \
  --url http://localhost:8080/api/genero/num-livros \
  --header 'User-Agent: insomnia/11.0.2'
```

### POST /avaliacao/

**Descrição:** Cria avaliação de um livro.

**Requisição:**
``` bash
curl --request POST \
  --url http://localhost:8080/api/avaliacao/ \
  --header 'Content-Type: application/json' \
  --header 'User-Agent: insomnia/11.0.2' \
  --data '{
	"livro_id": 3,
	"usuario_id": 1,
	"nota": 10
}'
```

**Resposta:**
``` json
{
	"id": 3,
	"livro_id": 3,
	"usuario_id": 1,
	"nota": 10
}
```

### GET /avaliacao/{id}

**Descrição:** Retorna uma avaliação a partir do ID.

**Requisição:**
``` bash
curl --request GET \
  --url http://localhost:8080/api/avaliacao/3 \
  --header 'User-Agent: insomnia/11.0.2'
```

**Resposta:**
``` json
{
	"id": 3,
	"livro_id": 3,
	"usuario_id": 1,
	"nota": 10
}
```

### PATCH /avaliacao/{id}

**Descrição:** Muda a nota de uma avaliação.

**Requisição:**
``` bash
curl --request PATCH \
  --url http://localhost:8080/api/avaliacao/3 \
  --header 'Content-Type: application/json' \
  --header 'User-Agent: insomnia/11.0.2' \
  --data '{
	"nota": 8
}'
```

**Body:**
``` json
{
	"nota": 8
}
```

**Resposta:**
``` json
{
	"id": 3,
	"livro_id": 3,
	"usuario_id": 1,
	"nota": 8
}
```

### DELETE /avaliacao/{id}

**Descrição:** Deleta uma avaliação a partir do ID.

**Requisição:**
``` bash
curl --request DELETE \
  --url http://localhost:8080/api/avaliacao/3 \
  --header 'User-Agent: insomnia/11.0.2'
```

### GET /avaliacao/livro-media

**Descrição:** Retorna a média das avaliações de um livro a partir do ID dele.

**Requisição:**
``` bash
curl --request GET \
  --url 'http://localhost:8080/api/avaliacao/livro-media?livro_id=3' \
  --header 'User-Agent: insomnia/11.0.2'
```

**Resposta:**
```10.0```

### GET /avaliacao/livro-usuario

**Descrição:** Retorna a avaliação de um livro por um usuário a partir de seus respectivos IDs.

**Requisição:**
``` bash
curl --request GET \
  --url 'http://localhost:8080/api/avaliacao/livro-usuario?livro_id=3&usuario_id=1' \
  --header 'User-Agent: insomnia/11.0.2'
```

**Resposta:**
``` json
{
	"id": 3,
	"livro_id": 3,
	"usuario_id": 1,
	"nota": 10
}
```

### GET /recomendacao/conteudo/{id}

**Descrição:** Retorna K (se não for enviado K o valor padrão é 10) recomendações filtradas por conteúdo para um usuário.

**Requisição:**
``` bash
curl --request GET \
  --url 'http://localhost:8080/api/recomendacao/conteudo/1?K=3' \
  --header 'User-Agent: insomnia/11.0.2'
```

**Resposta:**
``` json
[
	{
		"id": 4,
		"titulo": "O Cemitério",
		"subtitulo": null,
		"primeira_data_publicacao": null,
		"data_publicacao": 1985,
		"descricao": null,
		"capa": "https://covers.openlibrary.org/b/id/9047042-L.jpg",
		"paginas": null,
		"editora": "Suma de Letras",
		"autores": [
			{
				"id": 4,
				"nome": "Stephen King",
				"num_livros": null
			}
		],
		"generos": [
			{
				"id": 87,
				"nome": "Ficción",
				"num_livros": null
			},
			{
				"id": 34,
				"nome": "Large type books",
				"num_livros": null
			},
			{
				"id": 90,
				"nome": "fiction",
				"num_livros": null
			}
		]
	},
	{
		"id": 6,
		"titulo": "Extraordinário",
		"subtitulo": null,
		"primeira_data_publicacao": null,
		"data_publicacao": null,
		"descricao": null,
		"capa": "https://covers.openlibrary.org/b/id/12372729-L.jpg",
		"paginas": null,
		"editora": "Intrinseca",
		"autores": [
			{
				"id": 6,
				"nome": "R. J. Palacio",
				"num_livros": null
			}
		],
		"generos": [
			{
				"id": 50,
				"nome": "New York Times bestseller",
				"num_livros": null
			},
			{
				"id": 54,
				"nome": "Juvenile fiction",
				"num_livros": null
			},
			{
				"id": 22,
				"nome": "Spanish language materials",
				"num_livros": null
			},
			{
				"id": 4,
				"nome": "Fiction",
				"num_livros": null
			}
		]
	},
	{
		"id": 5,
		"titulo": "As Cinco Pessoas que Você Encontra no Céu",
		"subtitulo": null,
		"primeira_data_publicacao": null,
		"data_publicacao": 2005,
		"descricao": null,
		"capa": null,
		"paginas": null,
		"editora": "Sextante",
		"autores": [
			{
				"id": 5,
				"nome": "Mitch Albom",
				"num_livros": null
			}
		],
		"generos": [
			{
				"id": 2,
				"nome": "Open Library Staff Picks",
				"num_livros": null
			},
			{
				"id": 4,
				"nome": "Fiction",
				"num_livros": null
			},
			{
				"id": 16,
				"nome": "Psychological fiction",
				"num_livros": null
			}
		]
	}
]
```

## Códigos de Status

| Código | Significado             |
|--------|-------------------------|
| 200    | Requisição bem-sucedida |
| 201    | Recurso criado          |
| 400    | Erro de validação       |
| 401    | Não autorizado          |
| 404    | Não encontrado          |
| 500    | Erro interno do servidor|

---

## Exemplos de Erros

### 400 Bad Request

```json
{
	"error": "Bad Request",
	"message": "Campo 'titulo' é obrigatório.",
	"timestamp": "2025-05-05T17:37:33.2925759",
	"status": 400
}
```

### 404 Not Found

```json
{
	"error": "Not Found",
	"message": "Livro (id: 500) não encontrado.",
	"timestamp": "2025-05-05T17:37:33.2925759",
	"status": 404
}
```

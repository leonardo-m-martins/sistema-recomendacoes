USE sistema_recomendacoes;

-- Usuários do sistema
CREATE TABLE IF NOT EXISTS usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin UNIQUE NOT NULL,
    email VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin UNIQUE NOT NULL,
    senha VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL
);

-- Gêneros literários
CREATE TABLE IF NOT EXISTS genero (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin UNIQUE NOT NULL,
    num_livros INT
);

-- Autores dos livros
CREATE TABLE IF NOT EXISTS autor (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin UNIQUE NOT NULL,
    num_livros INT
);

-- Livros e metadados extraídos da Open Library
CREATE TABLE IF NOT EXISTS livro (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
    subtitulo VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
    data_publicacao SMALLINT,
    primeira_data_publicacao SMALLINT,
    descricao TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
    capa VARCHAR(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
    paginas INT,
    editora VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin
);

-- Adicionar índice FULLTEXT
ALTER TABLE livro ADD FULLTEXT (titulo, descricao);

-- Relação N:N entre livros e autores
CREATE TABLE IF NOT EXISTS livro_autor (
    livro_id INT,
    autor_id INT,
    PRIMARY KEY (livro_id, autor_id),
    FOREIGN KEY (livro_id) REFERENCES livro(id) ON DELETE CASCADE,
    FOREIGN KEY (autor_id) REFERENCES autor(id) ON DELETE CASCADE
);

-- Relação N:N entre livros e gêneros
CREATE TABLE IF NOT EXISTS livro_genero (
    genero_id INT,
    livro_id INT,
    PRIMARY KEY (genero_id, livro_id),
    FOREIGN KEY (genero_id) REFERENCES genero(id) ON DELETE CASCADE,
    FOREIGN KEY (livro_id) REFERENCES livro(id) ON DELETE CASCADE
);

-- Avaliações feitas por usuários
CREATE TABLE IF NOT EXISTS avaliacao (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT,
    livro_id INT,
    nota INT CHECK (nota >= 0 AND nota <= 10),
     UNIQUE (usuario_id, livro_id),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    FOREIGN KEY (livro_id) REFERENCES livro(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS vetor_livro (
    id INT PRIMARY KEY,
    vetor_generos LONGBLOB,          -- Tipo usado para armazenar o IntSet
    modulo_generos FLOAT,
    vetor_autores LONGBLOB,          -- Tipo usado para armazenar o IntSet
    modulo_autores FLOAT,
    paginas FLOAT,   -- Campo para o número de páginas
    ano FLOAT,   -- Campo para o ano de publicação
    FOREIGN KEY (id) REFERENCES livro(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS vetor_usuario (
    id INT PRIMARY KEY,
    vetor_generos LONGBLOB,          -- Tipo usado para armazenar o Int2FloatMap
    modulo_generos FLOAT,
    vetor_autores LONGBLOB,          -- Tipo usado para armazenar o Int2FloatMap
    modulo_autores FLOAT,
    paginas FLOAT,   -- Campo para o número de páginas (média)
    ano FLOAT,       -- Campo para o ano (média)
    FOREIGN KEY (id) REFERENCES usuario(id) ON DELETE CASCADE
);

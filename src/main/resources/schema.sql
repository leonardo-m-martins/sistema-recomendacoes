-- Usuários do sistema
CREATE TABLE IF NOT EXISTS usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL
);

-- Gêneros literários
CREATE TABLE IF NOT EXISTS genero (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) UNIQUE NOT NULL
);

-- Autores dos livros
CREATE TABLE IF NOT EXISTS autor (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL
);

-- Livros e metadados extraídos da Open Library
CREATE TABLE IF NOT EXISTS livro (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    subtitulo VARCHAR(255),
    data_publicacao SMALLINT,
    primeira_data_publicacao SMALLINT,
    descricao TEXT,
    capa VARCHAR(500),
    paginas INT,
    editora VARCHAR(255)
);

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
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    FOREIGN KEY (livro_id) REFERENCES livro(id) ON DELETE CASCADE
);

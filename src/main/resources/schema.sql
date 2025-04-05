CREATE TABLE IF NOT EXISTS usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS genero (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS livro (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    ano SMALLINT,
    pais_origem VARCHAR(255),
    descricao TEXT,
    capa VARCHAR(500),
    autor VARCHAR(255),
    paginas INT,
    editora VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS livro_genero (
    genero_id INT,
    livro_id INT,
    PRIMARY KEY (genero_id, livro_id),
    FOREIGN KEY (genero_id) REFERENCES genero(id) ON DELETE CASCADE,
    FOREIGN KEY (livro_id) REFERENCES livro(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS avaliacao (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT,
    livro_id INT,
    nota INT CHECK (nota >= 0 AND nota <= 10), -- nota de 0 a 10
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    FOREIGN KEY (livro_id) REFERENCES livro(id) ON DELETE CASCADE
);

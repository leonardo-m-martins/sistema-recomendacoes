-- Inserindo usuários
INSERT INTO usuario (nome, senha) VALUES 
('joao123', 'senha123'),
('maria456', 'senha456'),
('pedro789', 'senha789');

-- Inserindo gêneros
INSERT INTO genero (nome) VALUES 
('Ficção Científica'),
('Fantasia'),
('Mistério');

-- Inserindo livros
INSERT INTO livro (titulo, ano, pais_origem, descricao, capa, autor, paginas, editora) VALUES
('Duna', 1965, 'EUA', 'Um épico de ficção científica sobre poder, intriga e ecologia.', 'duna.jpg', 'Frank Herbert', 896, 'Aleph'),
('O Senhor dos Anéis', 1954, 'Reino Unido', 'Uma jornada épica na Terra Média.', 'lotr.jpg', 'J.R.R. Tolkien', 1200, 'HarperCollins'),
('Sherlock Holmes: Um Estudo em Vermelho', 1887, 'Reino Unido', 'A primeira história de Sherlock Holmes.', 'sherlock.jpg', 'Arthur Conan Doyle', 300, 'L&PM');

-- Associando livros a gêneros
INSERT INTO livro_genero (genero_id, livro_id) VALUES
(1, 7), -- Duna -> Ficção Científica
(2, 8), -- O Senhor dos Anéis -> Fantasia
(3, 9); -- Sherlock Holmes -> Mistério

-- Inserindo avaliações
INSERT INTO avaliacao (usuario_id, livro_id, nota) VALUES
(1, 7, 9), -- João avaliou Duna com 9
(2, 8, 10), -- Maria avaliou O Senhor dos Anéis com 10
(3, 9, 8); -- Pedro avaliou Sherlock Holmes com 8

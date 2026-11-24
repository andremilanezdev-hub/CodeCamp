-- Dados ficticios para desenvolvimento e demonstracao do CodeCamp.
-- Execute em um banco de desenvolvimento depois que o Hibernate criar as tabelas:
-- psql -d code_camp -f dados-iniciais.sql
-- A senha dos usuarios de exemplo e "CodeCamp@123".

BEGIN;

INSERT INTO empresa (id, razao_social, nome_fantasia, cnpj, inscricao_estaual, status)
VALUES
    (1, 'CodeCamp Tecnologia Ltda.', 'CodeCamp', '00.000.000/0001-01', 'ISENTO', 'A'),
    (2, 'Inova Aprendizagem Ltda.', 'InovaTech', '00.000.000/0001-02', 'ISENTO', 'A')
ON CONFLICT (id) DO NOTHING;

INSERT INTO usuario (
    id, nome, cpf, data_nascimento, data_cadastro, data_atualizacao, status, senha, empresa_id
)
VALUES
    (1, 'Ana Instrutora', '00000000001', '1988-04-12', '2025-01-10 09:00:00', NULL, 'A', 'CodeCamp@123', 1),
    (2, 'Bruno Instrutor', '00000000002', '1990-09-23', '2025-01-10 09:15:00', NULL, 'A', 'CodeCamp@123', 1),
    (3, 'Carla Instrutora', '00000000003', '1985-02-05', '2025-01-10 09:30:00', NULL, 'A', 'CodeCamp@123', 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO estudante (
    id, nome, email, cpf, cep, logradouro, complemento, bairro, localidade, uf, ddd,
    data_cadastro, data_atualizacao, status
)
VALUES
    (1, 'Daniel Estudante', 'daniel@example.com', '00000000101', '01001000', 'Praca da Se',
     'Apto 10', 'Se', 'Sao Paulo', 'SP', '11', '2025-02-01 10:00:00', NULL, 'A'),
    (2, 'Elisa Estudante', 'elisa@example.com', '00000000102', '20040002', 'Rua da Assembleia',
     'Casa', 'Centro', 'Rio de Janeiro', 'RJ', '21', '2025-02-02 10:00:00', NULL, 'A'),
    (3, 'Fabio Estudante', 'fabio@example.com', '00000000103', '80010000', 'Rua XV de Novembro',
     NULL, 'Centro', 'Curitiba', 'PR', '41', '2025-02-03 10:00:00', NULL, 'A')
ON CONFLICT (id) DO NOTHING;

INSERT INTO curso (
    id, nome, cargahoraria, data_cadastro, data_atualizacao, status, usuariocadastro_id
)
VALUES
    (1, 'Fundamentos de Java', 40, '2025-01-15 09:00:00', NULL, 'A', 1),
    (2, 'Desenvolvimento de APIs REST', 32, '2025-01-16 09:00:00', NULL, 'A', 2),
    (3, 'Banco de Dados com PostgreSQL', 24, '2025-01-17 09:00:00', NULL, 'A', 3)
ON CONFLICT (id) DO NOTHING;

INSERT INTO modulo (
    id, nome, cargahoraria, data_cadastro, data_atualizacao, status,
    empresa_id, curso_id, usuario_id
)
VALUES
    (1, 'Sintaxe e tipos de dados', 12, '2025-01-20 09:00:00', NULL, 'A', 1, 1, 1),
    (2, 'Orientacao a objetos', 16, '2025-01-21 09:00:00', NULL, 'A', 1, 1, 1),
    (3, 'Spring Boot e endpoints', 16, '2025-01-22 09:00:00', NULL, 'A', 1, 2, 2),
    (4, 'Validacao e tratamento de erros', 16, '2025-01-23 09:00:00', NULL, 'A', 1, 2, 2),
    (5, 'Modelagem relacional', 12, '2025-01-24 09:00:00', NULL, 'A', 2, 3, 3),
    (6, 'Consultas SQL', 12, '2025-01-25 09:00:00', NULL, 'A', 2, 3, 3)
ON CONFLICT (id) DO NOTHING;

INSERT INTO estudante_cursos (estudante_id, curso_id)
VALUES
    (1, 1),
    (1, 2),
    (2, 1),
    (3, 3)
ON CONFLICT DO NOTHING;

INSERT INTO conclusao_modulo (id, estudante_id, modulo_id, concluido, data_conclusao)
VALUES
    (1, 1, 1, TRUE, '2025-03-01 14:00:00'),
    (2, 1, 2, TRUE, '2025-03-05 14:00:00'),
    (3, 2, 1, TRUE, '2025-03-02 14:00:00')
ON CONFLICT (id) DO NOTHING;

INSERT INTO certificado (id, estudante_id, curso_id, codigo_validacao, data_emissao)
VALUES
    (1, 1, 1, '00000000-0000-4000-8000-000000000001', '2025-03-05 14:05:00')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('empresa', 'id'), (SELECT MAX(id) FROM empresa), TRUE);
SELECT setval(pg_get_serial_sequence('usuario', 'id'), (SELECT MAX(id) FROM usuario), TRUE);
SELECT setval(pg_get_serial_sequence('estudante', 'id'), (SELECT MAX(id) FROM estudante), TRUE);
SELECT setval(pg_get_serial_sequence('curso', 'id'), (SELECT MAX(id) FROM curso), TRUE);
SELECT setval(pg_get_serial_sequence('modulo', 'id'), (SELECT MAX(id) FROM modulo), TRUE);
SELECT setval(pg_get_serial_sequence('conclusao_modulo', 'id'), (SELECT MAX(id) FROM conclusao_modulo), TRUE);
SELECT setval(pg_get_serial_sequence('certificado', 'id'), (SELECT MAX(id) FROM certificado), TRUE);

COMMIT;

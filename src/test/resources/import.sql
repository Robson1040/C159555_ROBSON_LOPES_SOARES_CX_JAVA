-- Dados de teste básicos - Pessoas
INSERT INTO pessoa (nome, cpf, username, password, role) VALUES 
('Admin Test', '12345678901', 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN'),
('User Test', '98765432100', 'user', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USER');

-- Produtos de teste com valores corretos dos ENUMs
INSERT INTO produto (id, nome, tipo, tipo_rentabilidade, rentabilidade, periodo_rentabilidade, indice, liquidez, minimo_dias_investimento, fgc) VALUES
(1, 'Tesouro IPCA', 'TESOURO_DIRETO', 'POS', 6.5000, 'AO_ANO', 'IPCA', 1, 30, false),
(2, 'CDB Banco XYZ', 'CDB', 'POS', 12.5000, 'AO_ANO', 'CDI', 0, 30, true),
(3, 'Fundo DI Premium', 'FUNDO', 'POS', 10.8000, 'AO_ANO', 'CDI', 1, 0, false),
(4, 'CDB Pré-fixado', 'CDB', 'PRE', 13.2000, 'AO_ANO', 'NENHUM', 30, 90, true),
(5, 'LCI Habitacional', 'LCI', 'POS', 11.5000, 'AO_ANO', 'CDI', 90, 90, true);

-- Veículos de teste
INSERT INTO veiculo (id, status, brand, model, vehicle_year, engine) VALUES
(1, 'AVAILABLE', 'Toyota', 'Corolla', 2022, '1.8 Flex'),
(2, 'AVAILABLE', 'Honda', 'Civic', 2021, '2.0 Turbo'),
(3, 'RENTED', 'Volkswagen', 'Golf', 2020, '1.4 TSI'),
(4, 'UNDER_MAINTENANCE', 'Ford', 'Focus', 2019, '2.0 Flex'),
(5, 'AVAILABLE', 'Nissan', 'Sentra', 2023, '1.6 CVT');
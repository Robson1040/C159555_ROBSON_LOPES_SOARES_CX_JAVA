-- Criação da tabela de telemetria (caso não exista)
CREATE TABLE IF NOT EXISTS telemetria_metrica (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    endpoint VARCHAR(200) NOT NULL,
    contador_execucoes BIGINT NOT NULL DEFAULT 0,
    tempo_medio_resposta REAL NOT NULL DEFAULT 0.0,
    tempo_total_execucao REAL NOT NULL DEFAULT 0.0,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ultima_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices para performance da telemetria
CREATE INDEX IF NOT EXISTS idx_telemetria_endpoint ON telemetria_metrica(endpoint);
CREATE INDEX IF NOT EXISTS idx_telemetria_contador ON telemetria_metrica(contador_execucoes DESC);
CREATE INDEX IF NOT EXISTS idx_telemetria_data_criacao ON telemetria_metrica(data_criacao);

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

-- Dados de simulação de investimento para testes
INSERT INTO simulacao_investimento (cliente_id, produto, valor_investido, valor_final, prazo_meses, prazo_dias, prazo_anos, data_simulacao, rentabilidade_efetiva, rendimento, valor_simulado, cenario_simulacao) VALUES
(1, 'Tesouro IPCA', 1000.00, 1065.00, 12, 365, 1, '2024-01-15 10:30:00', 6.5000, 65.00, true, 'Cenário base - Inflação controlada'),
(2, 'CDB Banco XYZ', 5000.00, 5625.00, 12, 365, 1, '2024-02-20 14:45:00', 12.5000, 625.00, true, 'Cenário otimista - CDI em alta'),
(1, 'Fundo DI Premium', 2000.00, 2216.00, 12, 365, 1, '2024-03-10 09:15:00', 10.8000, 216.00, true, 'Cenário moderado'),
(2, 'CDB Pré-fixado', 10000.00, 11320.00, 12, 365, 1, '2024-04-05 16:20:00', 13.2000, 1320.00, true, 'Cenário pré-fixado'),
(1, 'Tesouro IPCA', 3000.00, 3195.00, 12, 365, 1, '2024-05-12 11:30:00', 6.5000, 195.00, true, 'Cenário conservador'),
(2, 'LCI Habitacional', 7500.00, 8362.50, 12, 365, 1, '2024-06-18 13:45:00', 11.5000, 862.50, true, 'Cenário isento IR');


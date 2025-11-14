-- Script para criação da tabela de métricas de telemetria
-- Compatível com SQLite (banco configurado no projeto)

CREATE TABLE IF NOT EXISTS telemetria_metrica (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    endpoint VARCHAR(200) NOT NULL,
    contador_execucoes BIGINT NOT NULL DEFAULT 0,
    tempo_medio_resposta REAL NOT NULL DEFAULT 0.0,
    tempo_total_execucao REAL NOT NULL DEFAULT 0.0,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ultima_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índice para busca rápida por endpoint
CREATE INDEX IF NOT EXISTS idx_telemetria_endpoint ON telemetria_metrica(endpoint);

-- Índice para consultas ordenadas por contador de execuções
CREATE INDEX IF NOT EXISTS idx_telemetria_contador ON telemetria_metrica(contador_execucoes DESC);

-- Índice para consultas por período
CREATE INDEX IF NOT EXISTS idx_telemetria_data_criacao ON telemetria_metrica(data_criacao);
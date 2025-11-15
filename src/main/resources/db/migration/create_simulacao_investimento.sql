-- Script DDL para criação da tabela simulacao_investimento
-- Usando SQLite como banco de dados

CREATE TABLE IF NOT EXISTS simulacao_investimento (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    cliente_id INTEGER NOT NULL,
    produto VARCHAR(255) NOT NULL,
    valor_investido DECIMAL(15,2) NOT NULL CHECK (valor_investido > 0),
    valor_final DECIMAL(15,2) NOT NULL CHECK (valor_final >= 0),
    prazo_meses INTEGER,
    prazo_dias INTEGER,
    prazo_anos INTEGER,
    data_simulacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    rentabilidade_efetiva DECIMAL(10,4),
    rendimento DECIMAL(15,2),
    valor_simulado BOOLEAN DEFAULT FALSE,
    cenario_simulacao VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para otimizar consultas
CREATE INDEX IF NOT EXISTS idx_simulacao_cliente_id ON simulacao_investimento(cliente_id);
CREATE INDEX IF NOT EXISTS idx_simulacao_data ON simulacao_investimento(data_simulacao DESC);
CREATE INDEX IF NOT EXISTS idx_simulacao_produto ON simulacao_investimento(produto);
CREATE INDEX IF NOT EXISTS idx_simulacao_valor_investido ON simulacao_investimento(valor_investido);

-- Comentários sobre a estrutura
-- id: Chave primária auto-incremento
-- cliente_id: ID do cliente que fez a simulação
-- produto: Nome do produto simulado
-- valor_investido: Valor inicial do investimento
-- valor_final: Valor final calculado na simulação
-- prazo_meses/prazo_dias/prazo_anos: Diferentes formas de especificar o prazo
-- data_simulacao: Quando a simulação foi realizada
-- rentabilidade_efetiva: Taxa de rentabilidade calculada
-- rendimento: Lucro obtido (valor_final - valor_investido)
-- valor_simulado: Se os valores foram gerados dinamicamente
-- cenario_simulacao: Descrição do cenário econômico simulado

-- Trigger para atualizar updated_at automaticamente (SQLite)
CREATE TRIGGER IF NOT EXISTS trigger_simulacao_updated_at
    AFTER UPDATE ON simulacao_investimento
BEGIN
    UPDATE simulacao_investimento 
    SET updated_at = CURRENT_TIMESTAMP 
    WHERE id = NEW.id;
END;
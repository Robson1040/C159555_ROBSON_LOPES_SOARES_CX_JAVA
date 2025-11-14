package org.example.model;

/**
 * Enum que define os tipos de renda dos produtos financeiros
 */
public enum TipoRenda {
    /**
     * Produtos de renda fixa - rendimento previsível
     */
    RENDA_FIXA,
    
    /**
     * Produtos de renda variável - rendimento não previsível
     */
    RENDA_VARIAVEL,
    
    /**
     * Produtos mistos - combinação de renda fixa e variável
     */
    MISTO
}
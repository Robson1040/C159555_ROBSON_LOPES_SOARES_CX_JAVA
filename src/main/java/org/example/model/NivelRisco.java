package org.example.model;

/**
 * Enum que define os níveis de risco dos produtos financeiros
 */
public enum NivelRisco {
    /**
     * Risco baixo - produtos garantidos pelo FGC
     */
    BAIXO,
    
    /**
     * Risco médio - produtos de renda fixa não garantidos pelo FGC
     */
    MEDIO,
    
    /**
     * Risco alto - produtos de renda variável não garantidos pelo FGC
     */
    ALTO,
    
    /**
     * Risco indeterminado - para casos não previstos
     */
    INDETERMINADO
}
package org.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para resposta do endpoint de perfil de risco do cliente
 */
public record PerfilRiscoResponse(
        @JsonProperty("clienteId")
        Long clienteId,
        
        @JsonProperty("perfil")
        String perfil,
        
        @JsonProperty("pontuacao")
        Integer pontuacao,
        
        @JsonProperty("descricao")
        String descricao
) {
    /**
     * Cria resposta para perfil Conservador
     */
    public static PerfilRiscoResponse conservador(Long clienteId, Integer pontuacao) {
        return new PerfilRiscoResponse(
                clienteId,
                "Conservador",
                pontuacao,
                "Perfil focado em segurança e liquidez, com baixa tolerância ao risco."
        );
    }
    
    /**
     * Cria resposta para perfil Moderado
     */
    public static PerfilRiscoResponse moderado(Long clienteId, Integer pontuacao) {
        return new PerfilRiscoResponse(
                clienteId,
                "Moderado",
                pontuacao,
                "Perfil equilibrado entre segurança e rentabilidade."
        );
    }
    
    /**
     * Cria resposta para perfil Agressivo
     */
    public static PerfilRiscoResponse agressivo(Long clienteId, Integer pontuacao) {
        return new PerfilRiscoResponse(
                clienteId,
                "Agressivo",
                pontuacao,
                "Perfil voltado para alta rentabilidade, com maior tolerância ao risco."
        );
    }
}
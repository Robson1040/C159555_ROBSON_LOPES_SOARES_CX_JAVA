package org.example.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.example.dto.ProdutoResponse;
import org.example.enums.NivelRisco;
import org.example.model.Produto;
import org.example.mapper.ProdutoMapper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço para recomendação de produtos baseado no perfil de risco
 */
@ApplicationScoped
public class ProdutoRecomendadoService {

    @Inject
    ProdutoMapper produtoMapper;

    /**
     * Busca produtos recomendados baseado no perfil de risco
     * 
     * @param perfil Perfil de risco (Conservador, Moderado, Agressivo)
     * @return Lista de produtos recomendados
     */
    public List<ProdutoResponse> buscarProdutosPorPerfil(String perfil) {
        if (perfil == null || perfil.trim().isEmpty()) {
            throw new IllegalArgumentException("Perfil não pode ser nulo ou vazio");
        }

        NivelRisco nivelRisco = mapearPerfilParaNivelRisco(perfil.trim());
        
        // Buscar todos os produtos e filtrar por nível de risco
        List<Produto> todosProdutos = Produto.listAll();
        
        List<Produto> produtosFiltrados = todosProdutos.stream()
                .filter(produto -> produto.getRisco() == nivelRisco)
                .collect(Collectors.toList());

        return produtoMapper.toResponseList(produtosFiltrados);
    }

    /**
     * Mapeia o perfil de risco para o enum NivelRisco
     * 
     * @param perfil String do perfil (Conservador, Moderado, Agressivo)
     * @return NivelRisco correspondente
     */
    private NivelRisco mapearPerfilParaNivelRisco(String perfil) {
        return switch (perfil.toLowerCase()) {
            case "conservador" -> NivelRisco.BAIXO;
            case "moderado" -> NivelRisco.MEDIO;
            case "agressivo" -> NivelRisco.ALTO;
            default -> throw new IllegalArgumentException("Perfil inválido: " + perfil + ". Valores aceitos: Conservador, Moderado, Agressivo");
        };
    }
}
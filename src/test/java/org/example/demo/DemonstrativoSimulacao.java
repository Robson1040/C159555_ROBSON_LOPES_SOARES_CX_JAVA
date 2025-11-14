package org.example.demo;

/**
 * Demonstração do endpoint de simulação de investimento
 * 
 * Este arquivo mostra exemplos de como usar o endpoint POST /simular-investimento
 */
public class DemonstrativoSimulacao {

    public static void main(String[] args) {
        System.out.println("=== DEMONSTRAÇÃO DO ENDPOINT DE SIMULAÇÃO ===\n");
        
        System.out.println("Endpoint: POST /simular-investimento\n");
        
        System.out.println("=== EXEMPLO 1: Simulação com filtros completos ===");
        String exemplo1 = """
            {
                "clienteId": 123456,
                "valor": 10000.00,
                "prazoMeses": 12,
                "tipoProduto": "CDB",
                "tipo_rentabilidade": "POS", 
                "indice": "CDI",
                "liquidez": 90,
                "fgc": true
            }
            """;
        System.out.println("Request:");
        System.out.println(exemplo1);
        
        System.out.println("Response esperado:");
        String response1 = """
            {
                "produtoValidado": {
                    "id": 1,
                    "nome": "CDB Banco XYZ",
                    "tipo": "CDB",
                    "tipo_rentabilidade": "POS",
                    "rentabilidade": 1.05,
                    "periodo_rentabilidade": "AO_ANO",
                    "indice": "CDI",
                    "liquidez": 90,
                    "minimo_dias_investimento": 30,
                    "fgc": true,
                    "risco": "BAIXO"
                },
                "resultadoSimulacao": {
                    "valorFinal": 11165.00,
                    "rentabilidadeEfetiva": 11.1825,
                    "prazoMeses": 12,
                    "prazoDias": null,
                    "prazoAnos": null,
                    "valorInvestido": 10000.00,
                    "rendimento": 1165.00
                },
                "dataSimulacao": "2025-11-14T10:30:00",
                "clienteId": 123456
            }
            """;
        System.out.println(response1);
        
        System.out.println("\n=== EXEMPLO 2: Simulação apenas com valor e prazo ===");
        String exemplo2 = """
            {
                "clienteId": 789012,
                "valor": 5000.00,
                "prazoAnos": 2
            }
            """;
        System.out.println("Request:");
        System.out.println(exemplo2);
        System.out.println("Descrição: Sem filtros - sistema escolhe o melhor produto disponível");
        
        System.out.println("\n=== EXEMPLO 3: Simulação com prazo em dias ===");
        String exemplo3 = """
            {
                "clienteId": 345678,
                "valor": 15000.00,
                "prazoDias": 180,
                "tipoProduto": "TESOURO_DIRETO"
            }
            """;
        System.out.println("Request:");
        System.out.println(exemplo3);
        
        System.out.println("\n=== EXEMPLO 4: Busca por produtos sem FGC ===");
        String exemplo4 = """
            {
                "clienteId": 555666,
                "valor": 25000.00,
                "prazoMeses": 24,
                "fgc": false
            }
            """;
        System.out.println("Request:");
        System.out.println(exemplo4);
        System.out.println("Descrição: Filtra apenas produtos sem garantia FGC");
        
        System.out.println("\n=== LÓGICA DE SELEÇÃO DO PRODUTO ===");
        System.out.println("1. Aplica todos os filtros informados na request");
        System.out.println("2. Filtra produtos que atendem ao prazo mínimo de investimento");
        System.out.println("3. Ordena por: 1º menor risco, 2º maior rentabilidade");
        System.out.println("4. Retorna o primeiro da lista (melhor opção)");
        
        System.out.println("\n=== CÁLCULO DA RENTABILIDADE ===");
        System.out.println("• Produtos PRÉ-FIXADOS: usa rentabilidade direta");
        System.out.println("• Produtos PÓS-FIXADOS: rentabilidade × taxa_do_índice");
        System.out.println("• Exemplo: 105% CDI = 1.05 × 10.65% = 11.18% ao ano");
        
        System.out.println("\n=== VALORES SIMULADOS DOS ÍNDICES ===");
        System.out.println("• SELIC: 10.75% ao ano");
        System.out.println("• CDI: 10.65% ao ano");
        System.out.println("• IBOVESPA: 8.50% ao ano");
        System.out.println("• IPCA: 4.25% ao ano");
        System.out.println("• IGP-M: 4.80% ao ano");
        
        System.out.println("\n=== CAMPOS OPCIONAIS NA REQUEST ===");
        System.out.println("Obrigatórios:");
        System.out.println("• clienteId: ID do cliente");
        System.out.println("• valor: Valor do investimento");
        System.out.println("• Um dos prazos: prazoMeses, prazoDias ou prazoAnos");
        System.out.println();
        System.out.println("Opcionais (filtros):");
        System.out.println("• tipoProduto: CDB, LCI, LCA, FUNDO, TESOURO_DIRETO, POUPANCA");
        System.out.println("• tipo_rentabilidade: PRE, POS");
        System.out.println("• indice: SELIC, CDI, IBOVESPA, IPCA, IGP_M, NENHUM");
        System.out.println("• liquidez: prazo para resgate (em dias)");
        System.out.println("• fgc: true/false para garantia FGC");
    }
}
#!/bin/bash
echo "=== TESTE COMPLETO DO SISTEMA DE TELEMETRIA CUSTOMIZADO ==="

echo ""
echo "1. Testando endpoint /telemetria antes de gerar métricas:"
curl -s http://localhost:9090/telemetria | jq .

echo ""
echo "2. Gerando métricas fazendo chamadas para endpoints:"

echo "   Fazendo chamada para /produtos..."
curl -s http://localhost:9090/produtos > /dev/null

echo "   Fazendo chamada para /clientes..."
curl -s http://localhost:9090/clientes > /dev/null

echo "   Fazendo mais chamadas para /produtos..."
curl -s http://localhost:9090/produtos > /dev/null
curl -s http://localhost:9090/produtos > /dev/null

echo "   Fazendo chamada para /simular-investimento..."
curl -s http://localhost:9090/simular-investimento > /dev/null

echo ""
echo "3. Testando endpoint /telemetria após gerar métricas reais:"
curl -s http://localhost:9090/telemetria | jq .

echo ""
echo "=== TESTE CONCLUÍDO ==="
#!/bin/bash

# Script shell para medir o throughput no enlace Switch -> Server
# Nadyan S. Pscheidt

# Habilitar as estatisticas:
curl http://127.0.0.1:8080/wm/statistics/config/enable/json -X POST

# Exp1 -> porta = 2
# Exp2 -> porta = ?

PORTA=2

for i in {0..9}
do
    # Executa 10 medicoes, uma a cada segundo
    curl http://127.0.0.1:8080/wm/statistics/bandwidth/00:00:00:00:00:00:00:01/$PORTA/json -X GET
    sleep 1 # espera 1 segundo
done

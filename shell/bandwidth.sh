#!/bin/bash

# Script shell para medir o throughput no enlace Switch -> Server
# Nadyan S. Pscheidt

# Habilitar as estatisticas:
curl http://127.0.0.1:8080/wm/statistics/config/enable/json -X POST

# Exp1 -> porta server = 2
# Exp2 -> porta server = 4

PORTA=4

for i in {0..119}
do
    # Executa 120 medicoes, uma a cada segundo
    curl http://127.0.0.1:8080/wm/statistics/bandwidth/00:00:00:00:00:00:00:01/$PORTA/json -X GET
    sleep 1 # espera 1 segundo
    echo " \n\n $i !!"
done

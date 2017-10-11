#!/bin/bash

# Nadyan Suriel Pscheidt - UDESC
# Abordagem para Distribuição de Vídeo Baseada em Redes Definidas por Software
# Script para a transmissão do vídeo helicopter.divx

# Cria a transmissão através do VLC utilizando RTP
COMANDO="vlc -vvv videos/helicopter.divx --sout '#rtp{mux=ts,dst='$IPCLI',sdp=sap}'"

# Executa
eval $COMANDO

#!/bin/bash

# Nadyan Suriel Pscheidt - UDESC
# Abordagem para Distribuição de Vídeo Baseada em Redes Definidas por Software
# Script para a transmissão do vídeo trailer.mp4

# Cria a transmissão através do VLC utilizando RTP
COMANDO="vlc -vvv videos/trailer.mp4 --sout '#rtp{mux=ts,dst='$REMOTE_ADDR',sdp=sap}'"

# Executa
eval $COMANDO

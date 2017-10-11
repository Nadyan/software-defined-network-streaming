#!/bin/bash

# Nadyan Suriel Pscheidt - UDESC
# Abordagem para Distribuição de Vídeo Baseada em Redes Definidas por Software
# Script para a tranmissão do vídeo coelho.sh

# Cria a transmissão através do VLC utilizando RTP 
COMANDO="vlc -vvv videos/video.mp4 --sout '#rtp{mux=ts,dst='$REMOTE_ADDR',sdp=sap}'"

# Executa a transmissão
eval $COMANDO

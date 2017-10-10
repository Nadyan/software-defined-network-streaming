#!/bin/bash

# Nadyan Suriel Pscheidt - UDESC
# Abordagem para Distribuição de Vídeo Baseada em Redes Definidas por Software

# IP do Cliente requisitante
IPCLI=$REMOTE_ADDR
NOMECLI=$REMOTE_USER

# Cria a transmissão através do VLC utilizando RTP
COMANDO="vlc -vvv helicopter.divx --sout '#rtp{mux=ts,dst='$IPCLI',sdp=sap}'"

# Executa
eval $COMANDO

echo "Content-type: text/html"
echo ""
echo "<html><head><title>Helicopter.divx</title></head></body>"
echo "Video Helicopter.divx sendo enviado para o cliente $NOMECLI com IP $IPCLI"
echo "</body></html>"


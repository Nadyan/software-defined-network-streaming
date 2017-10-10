#!/bin/bash

IPCLI=10.0.0.2 #verificar como pega o IP do cliente requisintante

# TODO: pega ip cliente 

#sudo vlc -vvv helicopter.divx --sout '#rtp{mux=ts,dst=$IPCLI,sdp=sap,name="reste"}'

echo "Content-type: text/html"
echo ""
echo "<html><head><title>Helicoptero.mp4</title></head></body>"
echo "Video Helicopter.divx sendo enviado para o cliente $IPCLI"
echo "</body></html>"

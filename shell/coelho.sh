#!/bin/bash

IPCLI=10.0.0.2 #verificar como pega o IP do cliente requisintante

# TODO: pega ip cliente 

#sudo vlc -vvv video.mp4 --sout '#rtp{mux=ts,dst=$IPCLI,sdp=sap,name="reste"}'

echo "Content-type: text/html"
echo ""
echo "<html><head><title>Coelho.mp4</title></head></body>"
echo "Video Coelho.mp4 sendo enviado para o cliente $IPCLI"
echo "</body></html>"

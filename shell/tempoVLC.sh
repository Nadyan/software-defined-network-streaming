#!/bin/bash

# Script shell para marcar o tempo de execucao do comando
# de stream do VLC

TEMPO1=$(($(date +%s%3N))) # tempo em ms

# comando pra executar o vlc:
vlc -vvv video.mp4 --sout '#rtp{mux=ts,dst=10.0.0.2,sdp=sap,name="teste"}'

TEMPO2=$(($(date +%s%3N)))

TEMPO3=$TEMPO2-$TEMPO1

echo $TEMPO3 ms

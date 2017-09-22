package net.floodlightcontroller.aggregator;

import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.MacAddress;
import org.projectfloodlight.openflow.types.TransportPort;

public class Request {
    
    /*  
     *  Classe Request que armazena as informações do request:
     *  - Qual vídeo foi requisitado;
     *  - MAC do usuário que requisitou o vídeo;
     *  - IP do usuário que requisitou o vídeo; e
     *  - Porta do Switch do usuário que requisitou o vídeo.
     */
    
    private String video;
    private MacAddress receiverMac;
    private IPv4Address receiverAddress;
    private TransportPort tcpPort;
    private int receiverPort;
    
    /* Construtores */
    public Request(String video, MacAddress receiverMac, IPv4Address receiverAddress, int receiverPort, TransportPort tcpPort) {
        this.video = video;
        this.receiverMac = receiverMac;
        this.receiverAddress = receiverAddress;
        this.receiverPort = receiverPort;
        this.tcpPort = tcpPort;
    }
    
    public Request(String video) {
        this.video = video;
    }
    
    public Request(String video, int receiverPort) {
        this.video = video;
        this.receiverPort = receiverPort;
    }
    
    public Request(String video, TransportPort tcpPort) {
        this.video = video;
        this.tcpPort = tcpPort;
    }
    
    /* Getters */
    public String getVideoId() {
        return video;
    }
    
    public MacAddress getMacAddress() {
        return receiverMac;
    }
    
    public IPv4Address getIpAddress() {
        return receiverAddress;
    }
    
    public int getPort() {
        return receiverPort;
    }
    
    public TransportPort getTcpPort() {
        return tcpPort;
    }
    
    /* TODO: Setters */
}


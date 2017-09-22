package net.floodlightcontroller.aggregator;

import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.MacAddress;
import org.projectfloodlight.openflow.types.TransportPort;

public class CompleteAddress {
    
    /* 
     * Classe para armazenar todos os dados de um usuário.
     * É utilizada para o a modificação do cabeçalho no módulo
     * ModifyPacketTCP.
     */
    
    private MacAddress serverMac;         
    private MacAddress clientMac;       
    private IPv4Address serverIp;      
    private IPv4Address clientIp;          
    private TransportPort serverPort;     
    private TransportPort clientPort;      
    
    /* Construtor */
    public CompleteAddress(MacAddress serverMac, MacAddress clientMac,
                           IPv4Address serverIp, IPv4Address clientIp,
                           TransportPort serverPort, TransportPort clientPort) {
        
        this.serverMac = serverMac;
        this.clientMac = clientMac;
        this.serverIp = serverIp;
        this.clientIp = clientIp;
        this.serverPort = serverPort;
        this.clientPort = clientPort;
    }
    
    public CompleteAddress() {
        
    }
    
    /* Getters */
    public MacAddress getSeverMac() {
        return serverMac;
    }
    
    public MacAddress getClientMac() {
        return clientMac;
    }
    
    public IPv4Address getServerIp() {
        return serverIp;
    }
    
    public IPv4Address getClientIp() {
        return clientIp;
    }
    
    public TransportPort getServerPort() {
        return serverPort;
    }
    
    public TransportPort getClientPort() {
        return clientPort;
    }
    
    /* Setters */
    public void setServerMac(MacAddress serverMac) {
        this.serverMac = serverMac;
    }
    
    public void setClientMac(MacAddress clientMac) {
        this.clientMac = clientMac;
    }
    
    public void setServerIp(IPv4Address serverIp) {
        this.serverIp = serverIp;
    }
    
    public void setClientIp(IPv4Address clientIp) {
        this.clientIp = clientIp;
    }
    
    public void setServerPort(TransportPort serverPort) {
        this.serverPort = serverPort;
    }
    
    public void setClientPort(TransportPort clientPort) {
        this.clientPort = clientPort;
    }
}

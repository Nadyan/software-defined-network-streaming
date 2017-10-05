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
    
    public MacAddress serverMac;         
    public MacAddress clientMac;       
    public IPv4Address serverIp;      
    public IPv4Address clientIp;          
    public TransportPort serverPort;     
    public TransportPort clientPort;
    public boolean flag;
    public int switchPort;
    public int ack;
    
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
    
    public CompleteAddress() {}
    
    /* Getters */
    public MacAddress getServerMac() {
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
    
    public boolean getFlag() {
        return flag;
    }
    
    public int getSwitchPort() {
        return switchPort;
    }
    
    public int getAck() {
        return ack;
    }
    
    /* Setters */
    public void setServerMac(MacAddress serverMac) {
        this.serverMac = serverMac;
    }
    
    public void setAck(int ack) {
        this.ack = ack;
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
    
    public void setFlag(boolean flag) {
        this.flag = flag;
    }
    
    public void setSwitchPort(int switchPort) {
        this.switchPort = switchPort;
    }
}


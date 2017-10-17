/**
 *    Copyright 2011, Big Switch Networks, Inc.
 *    Originally created by David Erickson, Stanford University
 *
 *    Licensed under the Apache License, Version 2.0 (the "License"); you may
 *    not use this file except in compliance with the License. You may obtain
 *    a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *    License for the specific language governing permissions and limitations
 *    under the License.
 **/

package net.floodlightcontroller.aggregator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.projectfloodlight.openflow.protocol.OFFactories;
import org.projectfloodlight.openflow.protocol.OFFactory;
import org.projectfloodlight.openflow.protocol.OFFlowAdd;
import org.projectfloodlight.openflow.protocol.OFFlowModFlags;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.protocol.OFVersion;
import org.projectfloodlight.openflow.protocol.action.OFAction;
import org.projectfloodlight.openflow.protocol.action.OFActionOutput;
import org.projectfloodlight.openflow.protocol.action.OFActionSetField;
import org.projectfloodlight.openflow.protocol.action.OFActions;
import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.protocol.match.MatchField;
import org.projectfloodlight.openflow.protocol.oxm.OFOxms;
import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.IpProtocol;
import org.projectfloodlight.openflow.types.MacAddress;
import org.projectfloodlight.openflow.types.OFBufferId;
import org.projectfloodlight.openflow.types.OFPort;
import org.projectfloodlight.openflow.types.TransportPort;
import org.projectfloodlight.openflow.types.U64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.packet.Data;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.HTTP;
import net.floodlightcontroller.packet.HTTPMethod;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.packet.TCP;
import net.floodlightcontroller.util.FlowModUtils;

public class AggregatorUDP implements IFloodlightModule, IOFMessageListener {

    /*
     * UDESC - Universidade do Estado de Santa Catarina
     * Bacharelado em Ciência da Computação
     * Abordagem para Distribuição de Vídeo Baseada em Redes Definidas por Software
     * Nadyan Suriel Pscheidt
     * 
     * Módulo responsável pela identificação de requests,
     * identificação de fluxos redundantes, 
     * agregação de fluxos redundantes
     * e criação de regras na tabela de fluxos.
     */
    
    private IFloodlightProviderService floodlightProvider;
    private static Logger logger;
    public static final TransportPort UDP_PORT = TransportPort.of(5004);
    
    /*  Listas de requisições */
    protected List<Request> requests;
    protected List<String> videosInTraffic;
    
    /* Endereços */
    private IPv4Address ipUser1 = IPv4Address.of("10.0.0.2");
    private IPv4Address ipUser2 = IPv4Address.of("10.0.0.3");
    private IPv4Address ipUser3 = IPv4Address.of("10.0.0.4");
    
    /* Portas */
    private int u1Port = 1;
    private int u2Port = 2;
    private int u3Port = 3;
    private int userPort = -1;
    
    @Override
    public String getName() {
        return AggregatorUDP.class.getSimpleName();
    }

    @Override
    public boolean isCallbackOrderingPrereq(OFType type, String name) {
        return false;
    }

    @Override
    public boolean isCallbackOrderingPostreq(OFType type, String name) {
        /* Deve ser executado antes do módulo Forwarding */
        
        if (type.equals(OFType.PACKET_IN) && name.equals("forwarding")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleServices() {
        return null;
    }

    @Override
    public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
        return null;
    }

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
        return null;
    }

    @Override
    public void init(FloodlightModuleContext context) throws FloodlightModuleException {
        floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
        logger = LoggerFactory.getLogger(AggregatorTCP.class);
        
        /* Criação das listas */
        requests = new ArrayList<Request>();                        // Lista com as informações dos clientes
        videosInTraffic = new ArrayList<String>();                  // Lista com os videos trafegando
    }

    @Override
    public void startUp(FloodlightModuleContext context) throws FloodlightModuleException {
        floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
    }
    
    @Override
    public Command receive(IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
        
        Ethernet eth = IFloodlightProviderService.bcStore.get(cntx, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
        
        MacAddress srcMac = eth.getSourceMACAddress();              // MAC cliente
        
        if (eth.getEtherType() == EthType.IPv4) {
            
            /* Pacote IPv4 */
                
            IPv4 ipv4 = (IPv4) eth.getPayload();
                
            IPv4Address srcIp = ipv4.getSourceAddress();            // IP cliente
            IPv4Address dstIp = ipv4.getDestinationAddress();       // IP server
        
            /* Define qual porta do switch pertence ao usuário */
            if (srcIp.equals(ipUser1) == true) {                    
                userPort = u1Port;
            } else if (srcIp.equals(ipUser2) == true) {
                userPort = u2Port;
            } else if (srcIp.equals(ipUser3) == true) {
                userPort = u3Port;
            }
            
            if (ipv4.getProtocol() == IpProtocol.TCP) {
                
                /* Pacote TCP com a requisição*/
                    
                TCP tcp = (TCP) ipv4.getPayload();
                
                /* Processa a requisicao */
                /* Pega header HTTP do payload do TCP */
                Data dt = (Data) tcp.getPayload();
                byte[] txt = dt.getData();
                String headerHttp = new String(txt);
                    
                /* Divisão do header em 3 partes: Método, URI e resto */
                String arr[] = headerHttp.split(" ", 3);
                String method = arr[0];
                String videoId;

                HTTP http = new HTTP();
                
                /* Identificação do método da requisição HTTP */
                if (method.compareTo("HEAD") == 0) {
                    http.setHTTPMethod(HTTPMethod.HEAD);
                } else if (method.compareTo("GET") == 0) {
                    http.setHTTPMethod(HTTPMethod.GET);
                } else {
                    http.setHTTPMethod(HTTPMethod.NONE);
                }
                
                if(http.getHTTPMethod() == HTTPMethod.GET) {
                    
                    /* Pacote GET com a requisição */
                        
                    videoId = arr[1].substring(arr[1].lastIndexOf("/") + 1);
                    
                    if (videosInTraffic.contains(videoId) == false) {
                            
                        /* Se ainda não havia uma transferência do vídeo requisitado */
                
                        Request novoRequest = new Request(videoId, srcMac, srcIp, userPort);
                            
                        videosInTraffic.add(videoId);  
                        requests.add(novoRequest);   
                        
                        logger.info("Requisição nova " + videoId + " de " + srcIp);
                            
                        return Command.CONTINUE;      
                    } else {
                        
                        /*  Se o vídeo requisitado já estava sendo transmitido, agrega os fluxos,
                         *  criando uma regra que recebe o fluxo do primeiro usuário e encaminha para
                         *  ele e os demais requisitantes do detrminado conteúdo.
                         */
                        
                        IPv4Address originalIp = srcIp;                                             // Valor de inicialização não necessariamente correto,
                                                                                                    // mas necessário. É atualizado no laço for com o valor correto
                        boolean getOriginal = false;
                        int contador = 0;
                        Request novoRequest = new Request(videoId, srcMac, srcIp, userPort);        // Adiciona o request na lista de requests
                        requests.add(novoRequest);                                                  // para a montagem da regra com todos os usuarios
                         
                        /* Inicio da montagem da regra.
                         * A regra possui as informações (MAC, IP e porta) dos clientes requisitantes.
                         * Com isso, o fluxo UDP destinado ao primeiro usuário é direcionado para todas as portas
                         * especificadas como OFActionOutput (primeiro usuário e demais requisitantes).
                         */
                            
                        OFFactory my13Factory = OFFactories.getFactory(OFVersion.OF_13);
                        OFActions actions = my13Factory.actions();
                        OFOxms oxms = my13Factory.oxms();
                        List<OFAction> actionsTo = new ArrayList<OFAction>();   // Lista que armazena as caracteristicas da regra
                                
                        /* Criação da regra com um laço For que percorre a lista de requests e verifica todos os de mesmo videoId */
                        for (int i = 0; i < requests.size(); i++) {
                            if (requests.get(i).getVideoId().equals(videoId)) {
                                /* Se for do mesmo videoId */
                                    
                                if (getOriginal == false) {
                                    /* Armazena o IP do primeiro requisitante para criacao do match,
                                     * que no caso é o usuário na qual o servidor está enviando o conteúdo */
                                    originalIp = requests.get(i).getIpAddress();
                                    getOriginal = true;
                                }
                                    
                                OFActionSetField setDstIp = actions.buildSetField()
                                                            .setField(oxms.buildIpv4Dst()
                                                            .setValue(requests.get(i).getIpAddress())
                                                            .build()).build();
                                    
                                OFActionSetField setDstMac = actions.buildSetField()
                                                             .setField(oxms.buildEthDst()
                                                             .setValue(requests.get(i).getMacAddress())
                                                             .build()).build();
                                    
                                OFActionOutput outputUser = actions.output(OFPort.of(requests.get(i).getPort()), Integer.MAX_VALUE);
                                    
                                actionsTo.add(setDstIp);
                                actionsTo.add(setDstMac);
                                actionsTo.add(outputUser);
                                
                                contador++;                                    // Quantidade de usuários recebendo o mesmo video
                            }
                        }
                            
                        OFFlowAdd flowTo = fluxoUDP(createMatch(sw, UDP_PORT, cntx, originalIp, dstIp), my13Factory, actionsTo, FlowModUtils.PRIORITY_HIGH);
                            
                        if (sw.write(flowTo)) {
                                
                            /* Se obter sucesso na escrita da regra */
                               
                            logger.info("Fluxos agregados para o request " + videoId + " com " + contador + " receptores");
                            
                            /*  Barra o pacote para ele nao seguir para o servidor,
                             *  pois já será transmitido para o usuário novo pela
                             *  regra flowTo que foi instalada. 
                             */
                            return Command.STOP;
                        } else {
                                
                            /* Se a escrita da regra falhar */
                                
                            logger.info("ERRO: Falha na agregação de fluxos para o request " + videoId 
                                         + ", o conteúdo será transmitido sem agregação!");
                                
                            /* Como nao conseguiu criar a regra, transmite normalmente sem agragação.
                             * O comando Continue irá enviar o request para o servidor */
                            return Command.CONTINUE;
                        }
                    }
                }
            } 
        }
        
        return Command.CONTINUE;
    }

    private Match createMatch(IOFSwitch sw, TransportPort port, FloodlightContext cntx, IPv4Address dst, IPv4Address src) {
        /* Criação do Match */
        
        Match.Builder mb = sw.getOFFactory().buildMatch();
        
        mb.setExact(MatchField.ETH_TYPE, EthType.IPv4)
                    .setExact(MatchField.IPV4_SRC, src)
                    .setExact(MatchField.IPV4_DST, dst)
                    .setExact(MatchField.IP_PROTO, IpProtocol.UDP)
                    .setExact(MatchField.UDP_DST, port);
        
        return mb.build();
    }
    
    private OFFlowAdd fluxoUDP(Match match, OFFactory myFactory, List<OFAction> actions, int prioridade) {
        /* Criação da regra */
        
        Set<OFFlowModFlags> flags = new HashSet<>();
        flags.add(OFFlowModFlags.SEND_FLOW_REM);
        
        OFFlowAdd flowToUDP = myFactory
                                .buildFlowAdd()
                                .setFlags(flags)
                                .setActions(actions)
                                .setBufferId(OFBufferId.NO_BUFFER)
                                .setIdleTimeout(60)
                                .setHardTimeout(0)
                                .setMatch(match)
                                .setCookie(U64.of(1L << 59))
                                .setPriority(prioridade)
                                .build();
        
        return flowToUDP;
    }
}





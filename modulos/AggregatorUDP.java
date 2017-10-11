package net.floodlightcontroller.aggregator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
     * 
     */
    
    private IFloodlightProviderService floodlightProvider;
    private static Logger logger;
    public static final TransportPort UDP_PORT = TransportPort.of(5004);
    private boolean repeat = false;
    
    /*  Listas de requisições */
    protected List<Request> requests;
    protected List<String> videosInTraffic;
    protected List<Request> gets;
    
    /* Endereços */
    private IPv4Address ipServer1 = IPv4Address.of("10.0.0.1");
    private IPv4Address ipUser1 = IPv4Address.of("10.0.0.2");
    private IPv4Address ipUser2 = IPv4Address.of("10.0.0.3");
    private IPv4Address ipUser3 = IPv4Address.of("10.0.0.4");
    private MacAddress macServer1 = MacAddress.of("00:00:00:00:00:01");
    private MacAddress macUser1 = MacAddress.of("00:00:00:00:00:02");
    private MacAddress macUser2 = MacAddress.of("00:00:00:00:00:03");
    private MacAddress macUser3 = MacAddress.of("00:00:00:00:00:04");
    
    /* Portas */
    private int s1Port = 4;
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
        /* Deve ser executado antes do Forwarding e ModifyPacketTCP */
        
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
        MacAddress dstMac = eth.getDestinationMACAddress();         // MAC server
        
        if (eth.getEtherType() == EthType.IPv4) {
            
            /* Pacote IPv4 */
                
            IPv4 ipv4 = (IPv4) eth.getPayload();
                
            IPv4Address srcIp = ipv4.getSourceAddress();            // IP cliente
            IPv4Address dstIp = ipv4.getDestinationAddress();       // IP server
        
            if (srcIp.equals(ipUser1) == true) {                     // Porta do Switch para cada usuário
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
                    
                    TransportPort srcPort = tcp.getSourcePort();    // porta cliente de origem do get
                    TransportPort tcpTranspPort = srcPort;          // porta do cliente que será enviado o video
                        
                    String uri = arr[1];
                    videoId = arr[1].substring(arr[1].lastIndexOf("/") + 1);
                    
                    if (videosInTraffic.contains(videoId) == false) {
                            
                        /* Se ainda não havia uma transferência do vídeo requisitado */
                
                        Request novoRequest = new Request(videoId, srcMac, srcIp, userPort, tcpTranspPort);
                            
                        videosInTraffic.add(videoId);  
                        requests.add(novoRequest);   
                        
                        logger.info("Requisição nova " + videoId + " de " + srcIp);
                            
                        return Command.CONTINUE;      
                    } else {
                        
                        /*  Se o vídeo requisitado já estava sendo transmitido, agrega os dois fluxos
                         *  - Primeiro usuário: Usuário que requisitou o vídeo inicialmente; e
                         *  - Segundo usuário: Usuário que requisitou o vídeo quando o mesmo já estava sendo transmitido
                         */
                
                        int index = -1;
                        Request searchKey = new Request(videoId);
                        index = Collections.binarySearch(requests, searchKey, new Comparador());    // Procura a posição na lista do vídeo
                        if (index == -1) {
                            logger.info("ERRO: ID de vídeo não encontrado");
                        }
                        IPv4Address originalIp = requests.get(index).getIpAddress();                // Recupera IP do primeiro usuário
                        MacAddress originalMac = requests.get(index).getMacAddress();               // Recupera MAC do primeiro usuário
                        int originalPort = requests.get(index).getPort();                           // Recupera Porta do primeiro usuário
                        
                        if (originalIp.equals(srcIp) == false && repeat == false) {
                            
                            /* Se for uma requisição de um mesmo video porém de outro cliente */

                            /* Inicio da montagem dos fluxos.
                             * OBS: O Fluxo com dois outputs (server -> cliente) não é necessário, 
                             * apenas o fluxo redirecionando os pacotes de vídeos sempre para o controlador para ser processado
                             */
                            
                            OFFactory my13Factory = OFFactories.getFactory(OFVersion.OF_13);
                            OFActions actions = my13Factory.actions();
                            OFOxms oxms = my13Factory.oxms();
                                
                            List<OFAction> actionsTo = new ArrayList<OFAction>();                  // Lista de actions para o fluxo do server para o cliente
                            
                            OFActionSetField setDstIPu2 = actions.buildSetField()
                                                                 .setField(oxms.buildIpv4Dst()
                                                                 .setValue(srcIp)
                                                                 .build()).build();
                            
                            OFActionSetField setDstMACu2 = actions.buildSetField()
                                                                  .setField(oxms.buildEthDst()
                                                                  .setValue(srcMac)
                                                                  .build()).build();
                            
                            OFActionSetField setDstIPu1 = actions.buildSetField()
                                                                 .setField(oxms.buildIpv4Dst()
                                                                 .setValue(originalIp)
                                                                 .build()).build();
                            
                            OFActionSetField setDstMAC1 = actions.buildSetField()
                                                                 .setField(oxms.buildEthDst()
                                                                 .setValue(originalMac)
                                                                 .build()).build();
                            
                            OFActionOutput outputUser1 = actions.output(OFPort.of(originalPort), Integer.MAX_VALUE);
                            OFActionOutput outputUser2 = actions.output(OFPort.of(userPort), Integer.MAX_VALUE);
                            
                            actionsTo.add(setDstIPu1);
                            actionsTo.add(setDstMAC1);
                            actionsTo.add(outputUser1);
                            
                            actionsTo.add(setDstIPu2);
                            actionsTo.add(setDstMACu2);
                            actionsTo.add(outputUser2);
                            
                            OFFlowAdd flowTo = fluxoUDP(createMatch(sw, UDP_PORT, cntx, originalIp, dstIp), my13Factory, actionsTo, FlowModUtils.PRIORITY_HIGH);
                            
                            sw.write(flowTo);
                            logger.info("Fluxos agregados para o request " + uri);
                            logger.info("Receptores nas portas: " + originalPort + " e " + userPort);
                            //repeat = true;
                            
                            /*  Barra o pacote para ele nao seguir para o servidor,
                             *  pois já será transmitido para o segundo usuário pelo
                             *  fluxo que foi instalado, que possui saida para o primeiro
                             *  e segundo usuário
                             */
                             return Command.STOP;
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

class Comparador implements Comparator<Request> {
    
    /* 
     *  Classe para comparar os itens das listas,
     *  tem como objetivo encontrar os itens
     */
    
    public int compare(Request e1, Request e2) {
        if(e1.getVideoId() == e2.getVideoId()) {
            return 1;  
        } else {
            return 0;
        }
    }
}


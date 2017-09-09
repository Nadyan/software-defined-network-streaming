package net.floodlightcontroller.aggregator;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.List;
import java.util.Set;
//import java.io.IOException;
//import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;

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
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.packet.TCP;
import net.floodlightcontroller.packet.HTTP;
import net.floodlightcontroller.packet.HTTPMethod;
import net.floodlightcontroller.util.FlowModUtils;

public class AggregatorTCP implements IFloodlightModule, IOFMessageListener {

	private IFloodlightProviderService floodlightProvider;
	private static Logger logger;
    public static final TransportPort HTTP_PORT = TransportPort.of(5001);
	
	//private IPv4Address ipServer1 = IPv4Address.of("10.0.0.1");
	private IPv4Address ipUser1 = IPv4Address.of("10.0.0.2");
	private IPv4Address ipUser2 = IPv4Address.of("10.0.0.3");
	private IPv4Address ipUser3 = IPv4Address.of("10.0.0.4");
	
	//private MacAddress macServer1 = MacAddress.of("00:00:00:00:00:01");
	//private MacAddress macUser1 = MacAddress.of("00:00:00:00:00:02");
	//private MacAddress macUser2 = MacAddress.of("00:00:00:00:00:03");
	//private MacAddress macUser3 = MacAddress.of("00:00:00:00:00:04");
	
	//private int s1Port = 4;
	private int u1Port = 1;
	private int u2Port = 2;
	private int u3Port = 3;
	int userPort = -1;
	
	@Override
	public String getName() {
		return AggregatorTCP.class.getSimpleName();
	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		return false;
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
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
	}

	@Override
	public void startUp(FloodlightModuleContext context) throws FloodlightModuleException {
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
	}

	@Override
	public Command receive(IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
		
		Ethernet eth = IFloodlightProviderService.bcStore.get(cntx, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
		
		MacAddress srcMac = eth.getSourceMACAddress();				// client mac
		//MacAddress dstMac = eth.getDestinationMACAddress();		// server mac
		
		if(eth.getEtherType() == EthType.IPv4) {
			IPv4 ipv4 = (IPv4) eth.getPayload();
			
			IPv4Address srcIp = ipv4.getSourceAddress();			// client IP
			IPv4Address dstIp = ipv4.getDestinationAddress();		// server IP
			
			if(ipv4.getProtocol() == IpProtocol.TCP) {
				TCP tcp = (TCP) ipv4.getPayload();
				
				//TransportPort srcPort = tcp.getSourcePort();		// client port
				TransportPort dstPort = tcp.getDestinationPort();	// server port

				/* get http header */
				Data dt = (Data)tcp.getPayload();
				byte[] txt = dt.getData();
				String headerHttp = new String(txt);
				/* divisao da string */
				String arr[] = headerHttp.split(" ", 3);
				String method = arr[0];
				
				HTTP http = new HTTP();
				
				if(method.compareTo("HEAD") == 0) {
					http.setHTTPMethod(HTTPMethod.HEAD);
				} else if(method.compareTo("GET") == 0) {
					http.setHTTPMethod(HTTPMethod.GET);
				} else {
					http.setHTTPMethod(HTTPMethod.NONE);
				}
					
				if(http.getHTTPMethod() == HTTPMethod.HEAD) {
					String uri = arr[1];
					//String resto = arr[2];

					logger.info("--- Novo HEAD ---");
					logger.info("Method: " + method);
					logger.info("URI: " + uri);
					//logger.info("Resto: " + resto);
					//logger.info("Metodo: " + http.getHTTPMethod());
					logger.info("-----------------");
					
					if(srcIp == ipUser1) {
                        userPort = u1Port;
                    } else if(srcIp == ipUser2) {
                        userPort = u2Port;
                    } else if(srcIp == ipUser3) {
                        userPort = u3Port;
                    }
					
					List<Request> requests = new ArrayList<Request>();            // lista com as informações dos clientes
					List<String> videosInTraffic = new ArrayList<String>();       // lista com os videos trafegando
					
					Request novoRequest = new Request(uri, srcMac, srcIp, userPort);
					
					if(videosInTraffic.contains(uri) == false) {				  // se ainda nao estava na lista
						/* cria um novo fluxo */
						
					    /* Provavelmente, aqui dentro desse if, vai ficar apenas:
					     * request.add(novoRequest);
					     * logger.info("Novo request identificado!\n");
					     * return Command.CONTINUE; 
					     * 
					     * pois nao é necessario criar um fluxo para uma transmissao nova,
					     * apenas para a agregação de dois fluxos.
					     */
					    
						OFFactory my13Factory = OFFactories.getFactory(OFVersion.OF_13);
						OFActions actions = my13Factory.actions();
						OFOxms oxms = my13Factory.oxms();
						
						List<OFAction> actionsTo = new ArrayList<OFAction>();
						
						OFActionSetField setDstIp = actions.buildSetField()
						                            .setField(oxms.buildIpv4Dst()
						                            .setValue(srcIp)
						                            .build()).build();
						
						OFActionSetField setDstMac = actions.buildSetField()
						                             .setField(oxms.buildEthDst()
						                             .setValue(srcMac)
						                             .build()).build();    			
						
						OFActionOutput outputClient = actions.output(OFPort.of(userPort), Integer.MAX_VALUE);
						
						actionsTo.add(setDstIp);
						actionsTo.add(setDstMac);
						actionsTo.add(outputClient);
						
						OFFlowAdd flowToTCP = fluxoTCP(createMatchFromPacket(sw, dstPort, cntx, dstIp, srcIp), my13Factory, actionsTo);
			
						videosInTraffic.add(uri);
						requests.add(novoRequest);
						sw.write(flowToTCP);
						logger.info("\nRequisição nova! URI: " + uri + "\n");
						
						return Command.CONTINUE;
					} else {
						/* agrega com fluxo ja existente */
					    
					    /* busca na lista o index do elemento que tem o request original
					     * https://www.java2novice.com/java-collections-and-util/collections/binary-search/
					     */
					    
					    int index = -1;
					    Request searchKey = new Request(uri);
					    index = Collections.binarySearch(requests, searchKey, new Comparador());
					    if(index != -1) {
					        logger.info("URI encontrado na posição " + index);
					    }
					    IPv4Address originalIp = requests.get(index).getIpAddress();
					    MacAddress originalMac = requests.get(index).getMacAddress();
					    int originalPort = requests.get(index).getPort();
						
					    /* Inicio montagem do fluxo */
					    OFFactory my13Factory = OFFactories.getFactory(OFVersion.OF_13);
					    OFActions actions = my13Factory.actions();
					    OFOxms oxms = my13Factory.oxms();
						
					    List<OFAction> actionsTo = new ArrayList<OFAction>();
					    
					    OFActionSetField setDstIp1 = actions.buildSetField()
					                                 .setField(oxms.buildIpv4Dst()
					                                 .setValue(srcIp)
					                                 .build()).build();
					    
					    OFActionSetField setDstMac1 = actions.buildSetField()
					                                  .setField(oxms.buildEthDst()
					                                  .setValue(srcMac)
					                                  .build()).build();
					    
					    OFActionSetField setDstIp2 = actions.buildSetField()
					                                 .setField(oxms.buildIpv4Dst()
					                                 .setValue(originalIp)
					                                 .build()).build();
					    
					    OFActionSetField setDstMac2 = actions.buildSetField()
					                                  .setField(oxms.buildEthDst()
					                                  .setValue(originalMac)
					                                  .build()).build();

					    OFActionOutput outputClient1 = actions.output(OFPort.of(userPort), Integer.MAX_VALUE);
					    OFActionOutput outputClient2 = actions.output(OFPort.of(originalPort), Integer.MAX_VALUE);
					    
					    actionsTo.add(setDstIp1);
					    actionsTo.add(setDstMac1);
					    actionsTo.add(outputClient1);
					    actionsTo.add(setDstIp2);
					    actionsTo.add(setDstMac2);
					    actionsTo.add(outputClient2);
					    
					    OFFlowAdd flowToTCP = fluxoTCP(createMatchFromPacket(sw, dstPort, cntx, originalIp, dstIp), my13Factory, actionsTo);
					    
					    sw.write(flowToTCP);
					    logger.info("Fluxos agregados para o request " + uri);
					    
						return Command.STOP;
					}
				} else {
					return Command.CONTINUE;
				}
			}
		}
		return Command.CONTINUE;
	}
	
	private OFFlowAdd fluxoTCP(Match match, OFFactory myFactory, List<OFAction> actions) {
		Set<OFFlowModFlags> flags = new HashSet<>();
		flags.add(OFFlowModFlags.SEND_FLOW_REM);
		
		OFFlowAdd flowToTCP = myFactory
		                      .buildFlowAdd()
		                      .setActions(actions)
		                      .setBufferId(OFBufferId.NO_BUFFER)
		                      .setIdleTimeout(13)
		                      .setHardTimeout(0)
		                      .setMatch(match)
		                      .setCookie(U64.of(1L << 59))
		                      .setPriority(FlowModUtils.PRIORITY_HIGH)
		                      .build();
		return flowToTCP;
	}
	
	private Match createMatchFromPacket(IOFSwitch sw, TransportPort port, FloodlightContext cntx, IPv4Address srcIp, IPv4Address dstIp) {
		Match.Builder mb = sw.getOFFactory().buildMatch();
		
		mb.setExact(MatchField.ETH_TYPE, EthType.IPv4)
	        .setExact(MatchField.IPV4_SRC, dstIp) // ip server
	        .setExact(MatchField.IPV4_DST, srcIp) // ip client
	        .setExact(MatchField.IP_PROTO, IpProtocol.TCP)
	        .setExact(MatchField.TCP_DST, port);
	
		return mb.build();
	}
}

class Request {
	private String video;
	private MacAddress receiverMac;
	private IPv4Address receiverAddress;
	private int receiverPort;
	
	public Request(String video, MacAddress receiverMac, IPv4Address receiverAddress, int receiverPort) {
		this.video = video;
		this.receiverMac = receiverMac;
		this.receiverAddress = receiverAddress;
		this.receiverPort = receiverPort;
	}
	
	public Request(String video) {
	    this.video = video;
	}
	
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
}

class Comparador implements Comparator<Request> {
    public int compare(Request e1, Request e2) {
        if(e1.getVideoId() == e2.getVideoId()) {
            return 1;  
        } else {
            return 0;
        }
    }
}






























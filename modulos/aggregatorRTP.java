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
import net.floodlightcontroller.duplicate.DuplicateUDP;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.packet.UDP;
import net.floodlightcontroller.util.FlowModUtils;

public class AggregatorRTP implements IFloodlightModule, IOFMessageListener {

	private IFloodlightProviderService floodlightProvider;
	private static Logger logger;

	private IPv4Address ipServer1 = IPv4Address.of("10.0.0.1");
	private IPv4Address ipUser1 = IPv4Address.of("10.0.0.2");
	private IPv4Address ipUser2 = IPv4Address.of("10.0.0.3");
	private IPv4Address ipUser3 = IPv4Address.of("10.0.0.4");

	private MacAddress macServer1 = MacAddress.of("00:00:00:00:00:01");
	private MacAddress macUser1 = MacAddress.of("00:00:00:00:00:02");
	private MacAddress macUser2 = MacAddress.of("00:00:00:00:00:03");
	private MacAddress macUser3 = MacAddress.of("00:00:00:00:00:04");
	
	private int s1Port = 4;
	private int u1Port = 1;
	private int u2Port = 2;
	private int u3Port = 3;
	
	@Override
	public String getName() {
		return AggregatorRTP.class.getSimpleName();
	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		return false;
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
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
		logger = LoggerFactory.getLogger(AggregatorRTP.class);
	}

	@Override
	public void startUp(FloodlightModuleContext context) throws FloodlightModuleException {
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
	}
	
	@Override
	public Command receive(IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {

		/* Aqui começa a brincadeira */
		
		Ethernet eth = IFloodlightProviderService.bcStore.get(cntx, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
		
		Set<IPv4> fluxos = new HashSet<IPv4>(); // lista de fluxos ativos
		
		if(eth.getEtherType() == EthType.IPv4) {
			
			IPv4 ipv4 = (IPv4) eth.getPayload();
			
			IPv4Address srcIp = ipv4.getSourceAddress();
			IPv4Address dstIp = ipv4.getDestinationAddress();
			
			if(ipv4.getProtocol() == IpProtocol.UDP) {
			
				UDP udp = (UDP) ipv4.getPayload();
				
				if(srcIp.compareTo(ipServer1) == 0) { // se a origem for do server1
				
					if(fluxos.add(ipv4)) { // se adicionou na lista e ainda nao existia
						return Command.CONTINUE;
					}else {
						/* agregacao de dois fluxos */
						
						/* procura no set o fluxo igual */
						
						/* cria um fluxo unico para os dois com dois outputs */
					}
				}
			}
		}
		
		return Command.CONTINUE;
	}
}

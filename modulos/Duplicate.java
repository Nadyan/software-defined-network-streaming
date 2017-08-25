package net.floodlightcontroller.duplicate;

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
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.packet.UDP;
import net.floodlightcontroller.util.FlowModUtils;

/*
 * Nadyan Suriel Pscheidt - UDESC
 * 
 * Este módulo foi construido com a finalidade de testar a duplicacao de fluxos,
 * o fluxo vindo de server1 é duplicado para o user1 e user2
 */

public class Duplicate implements IOFMessageListener, IFloodlightModule {

	private IFloodlightProviderService floodlightProvider;
	private static Logger logger;

	private IPv4Address ipServer1 = IPv4Address.of("10.0.0.1");
	private IPv4Address ipUser1 = IPv4Address.of("10.0.0.2");
	private IPv4Address ipUser2 = IPv4Address.of("10.0.0.3");
	//private IPv4Address ipUser3 = IPv4Address.of("10.0.0.4");

	//private MacAddress macServer1 = MacAddress.of("00:00:00:00:00:01");
	private MacAddress macUser1 = MacAddress.of("00:00:00:00:00:02");
	private MacAddress macUser2 = MacAddress.of("00:00:00:00:00:03");
	//private MacAddress macUser3 = MacAddress.of("00:00:00:00:00:04");
	
	//private int s1Port = 4;
	private int u1Port = 1;
	private int u2Port = 2;
	//private int u3Port = 3;
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return DuplicateUDP.class.getSimpleName();
	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		Collection<Class<? extends IFloodlightService>> l = 
				new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IFloodlightProviderService.class);
		return l;
	}

	@Override
	public void init(FloodlightModuleContext context) throws FloodlightModuleException {
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
		logger = LoggerFactory.getLogger(Duplicate.class);
	}

	@Override
	public void startUp(FloodlightModuleContext context) throws FloodlightModuleException {
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
	}

	@Override
	public Command receive(IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
		
		Ethernet eth = IFloodlightProviderService.bcStore.get(cntx, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
		
		if(eth.getEtherType() == EthType.IPv4) {
			IPv4 ipv4 = (IPv4) eth.getPayload();
			
			IPv4Address srcIp = ipv4.getSourceAddress();
			IPv4Address dstIp = ipv4.getDestinationAddress();
			
			if(ipv4.getProtocol() == IpProtocol.UDP) {
				UDP udp = (UDP) ipv4.getPayload();
				
				if(srcIp.compareTo(ipServer1) == 0) { // se a origem for server1
					TransportPort dstPort = udp.getDestinationPort();
					
					OFFactory my13Factory = OFFactories.getFactory(OFVersion.OF_13);
					OFActions actions = my13Factory.actions();
					OFOxms oxms = my13Factory.oxms();
					
					List<OFAction> actionsTo = new ArrayList<OFAction>();
					
					OFActionSetField setDstIPu2 = actions.buildSetField()
									.setField(oxms.buildIpv4Dst()
									.setValue(ipUser2)
									.build()).build();
					
					OFActionSetField setDstMACu2 = actions.buildSetField()
									.setField(oxms.buildEthDst()
									.setValue(macUser2)
									.build()).build();
					
					OFActionSetField setDstIPu1 = actions.buildSetField()
							.setField(oxms.buildIpv4Dst()
							.setValue(ipUser1)
							.build()).build();
					
					OFActionSetField setDstMACu1 = actions.buildSetField()
							.setField(oxms.buildEthDst()
							.setValue(macUser1)
							.build()).build();
							
					OFActionOutput outputUser1 = actions.output(OFPort.of(u1Port), Integer.MAX_VALUE);
					OFActionOutput outputUser2 = actions.output(OFPort.of(u2Port), Integer.MAX_VALUE);
					
					actionsTo.add(setDstIPu2);
					actionsTo.add(setDstMACu2);
					actionsTo.add(outputUser2);
					
					actionsTo.add(setDstIPu1);
					actionsTo.add(setDstMACu1);
					actionsTo.add(outputUser1);
					
					OFFlowAdd flowTo = fluxoUDP(createMatchFromPacket(sw, dstPort, cntx, dstIp), my13Factory, actionsTo, FlowModUtils.PRIORITY_HIGH);
					
					sw.write(flowTo);
					logger.info("Fluxos instalados\n");
				}
			}
		}
		
		return Command.CONTINUE;
	}
	
	private OFFlowAdd fluxoUDP(Match match, OFFactory myFactory, List<OFAction> actions, int prioridade) {
		
		/* Criação do fluxo */
		
		Set<OFFlowModFlags> flags = new HashSet<>();
		flags.add(OFFlowModFlags.SEND_FLOW_REM);
		
		OFFlowAdd flowToUDP = myFactory
				.buildFlowAdd()
				.setFlags(flags)
				.setActions(actions)
				.setBufferId(OFBufferId.NO_BUFFER)
				.setIdleTimeout(5)
				.setHardTimeout(5)
				.setMatch(match)
				.setCookie(U64.of(1L << 59))
				.setPriority(prioridade)
				.build();
		
		return flowToUDP;
	}
	
	private Match createMatchFromPacket(IOFSwitch sw, TransportPort port, FloodlightContext cntx, IPv4Address dst) {
		
		/* criação do match */
		
		Match.Builder mb = sw.getOFFactory().buildMatch();
		
		mb.setExact(MatchField.ETH_TYPE, EthType.IPv4)
			.setExact(MatchField.IPV4_SRC, ipServer1)
			.setExact(MatchField.IPV4_DST, dst)
			.setExact(MatchField.IP_PROTO, IpProtocol.UDP)
			.setExact(MatchField.UDP_DST, port);
		
		return mb.build();
	}
}








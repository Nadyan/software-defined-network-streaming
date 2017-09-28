package net.floodlightcontroller.packet;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class HTTP extends BasePacket {
	
	/*
	 * Author: Nadyan Suriel Pscheidt
	 */
	
	//private static final int HTTP_HEADER_INIT_BYTE = 66;
	
	private HTTPMethod method;
	private String methodRead;
	//private Byte[] uri;
	
	/*
	 * 
	 * TODO: Demais campos
	 * 
	 */

	private static final int HEADER_SIZE = 241;

	@Override
	public byte[] serialize() {
		
		resetChecksum();
		
		byte[] data = new byte[HEADER_SIZE];
		ByteBuffer bf = ByteBuffer.wrap(data);
		
		/* TODO: serialize */
		
		//bf.put(((Data) payload).getData());
		
		return data;
	}

	@Override
	public IPacket deserialize(byte[] data, int offset, int length) throws PacketParsingException {

		// * | METHOD | | URI | *
		 
		byte[] b = Arrays.copyOfRange(data, offset, data.length);		// pega todo o header
        String httpHeader = new String(b);								// passa para uma string
		
		if(methodRead.equals(HTTPMethod.HEAD.getLabel())) { 			// "HEAD"
			this.method = HTTPMethod.HEAD;
		} else if(methodRead.equals(HTTPMethod.GET.getLabel())){		// "GET"
			this.method = HTTPMethod.GET;
		} else if(methodRead.equals(HTTPMethod.POST.getLabel())) {		// "POST"
			this.method = HTTPMethod.POST;
		} else if(methodRead.equals(HTTPMethod.PUT.getLabel())) {		// "PUT"
			this.method = HTTPMethod.PUT;
		} else if(methodRead.equals(HTTPMethod.DELETE.getLabel())) {	// "DELETE"
			this.method = HTTPMethod.DELETE;
		} else if(methodRead.equals(HTTPMethod.CONNECT.getLabel())) {	// "CONNECT"
			this.method = HTTPMethod.CONNECT;
		} else if(methodRead.equals(HTTPMethod.OPTIONS.getLabel())) {	// "OPTIONS"
			this.method = HTTPMethod.OPTIONS;
		} else if(methodRead.equals(HTTPMethod.TRACE.getLabel())) {		// "TRACE"
			this.method = HTTPMethod.TRACE;
		} else {
			this.method = HTTPMethod.NONE;
		}
		
		return this;
	}
	
	public HTTPMethod getHTTPMethod() {
		return method;
	}

	public void setHTTPMethod(HTTPMethod method) {
		this.method = method;
	}
}

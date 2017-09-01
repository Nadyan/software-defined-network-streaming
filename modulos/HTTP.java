package net.floodlightcontroller.packet;

import java.nio.ByteBuffer;

public class HTTP extends BasePacket {
	
	private HTTPMethod method;
	private Byte[] uri;
	
	/*
	 * 
	 * TODO: Demais campos
	 * 
	 */

	private static final int HEADER_SIZE = 12; // FIX HEADER SIZE

	@Override
	public byte[] serialize() {
		
		resetChecksum();
		
		byte[] data = new byte[HEADER_SIZE];
		ByteBuffer bf = ByteBuffer.wrap(data);
		bf.putInt(this.method.type);
		
		for(int i = 0; i < uri.length; i++) {
			bf.put(this.uri[i]);
		}
		
		//bf.put(((Data) payload).getData());
		return data;
	}

	@Override
	public IPacket deserialize(byte[] data, int offset, int length) throws PacketParsingException {

		ByteBuffer bb = ByteBuffer.wrap(data, offset, length - HEADER_SIZE);
		byte b = bb.get();
		
		// 1 byte
		method = HTTPMethod.getType(b & 0x7F);
		
		if(method.compareTo(HTTPMethod.HEAD) == 0) {
		
			bb.asCharBuffer();
			
			for(int i = 0; i < uri.length; i++) {
				// 35 bytes
				uri[i] = bb.get();
			}
		}
		
		return this;
	}
	
	public HTTPMethod getHTTPMethod() {
		return method;
	}

	public void setHTTPMethod(HTTPMethod method) {
		this.method = method;
	}
	
	public Byte[] getUri() {
		return uri;
	}
	
	public void setUri(Byte[] uri) {
		this.uri = uri;
	}
}

package net.floodlightcontroller.packet;

import java.nio.ByteBuffer;

public class MP2T extends BasePacket  {

	/*
	 * Author: Nadyan Suriel Pscheidt
	 * 
	 * MP2T (MPEG-II TS) header fields
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-  
	 * | Sync| Transp.| Payload   | Transp. |     | Transp.| Adap. | cont. | Adapt.||         |
	 * | Byte| Error  | Unit Start| Priority| PID |  Scr.  | field | count.| field || Payload |
	 * |     | Indic. | indicator |         |     | Contr. | contr.|       |       ||         |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
	 * |  8b |    1b  |     1b    |    1b   | 13b |   2b   |   2b  |   4b  |       ||   184B
	 */
	
	// 8 bits
	private int syncByte;
	
	// 1 bit
	private boolean transpErrInd;
	
	// 1 bit
	private boolean payloadUnitInd;
	
	// 1 bit
	private boolean transpPriority;
	
	// 13 bits
	private int pid;

	// 2 bits
	private int transpScrCtl;
	
	// 2 bits
	private int adapFieldCtl;
	
	// 4 bits
	private int contCount;
	
	// 32 bits total
	private static final int HEADER_SIZE = 32;
	
	@Override 
	public byte[] serialize() {
		resetChecksum();
		
		byte[] data = new byte[HEADER_SIZE];
		ByteBuffer bf = ByteBuffer.wrap(data);
		
		bf.putInt(this.syncByte);
		bf.putInt(this.transpErrInd ? 0:1);
		bf.putInt(this.payloadUnitInd ? 0:1);
		bf.putInt(this.transpPriority ? 0:1);
		bf.putInt(this.pid);
		bf.putInt(this.transpScrCtl);
		bf.putInt(this.adapFieldCtl );
		bf.putInt(this.contCount);
		bf.put(((Data) payload).getData());
		
		return data;
	}

	@Override
	public IPacket deserialize(byte[] data, int offset, int length) throws PacketParsingException {
		// MP2T header inits on byte ??
		/*if(length < ??) {
			throw new PacketParsingException("Not an MP2T packet");
		}*/
		
		ByteBuffer bb = ByteBuffer.wrap(data, offset, length - HEADER_SIZE);
		byte b = bb.get();
		
		// 1111 1111 0000 0000 0000 0000 0000 0000
		syncByte = b & 0xFF000000;
		
		// 0000 0000 1000 0000 0000 0000 0000 0000
		transpErrInd = (b & 0x00800000) > 0;
		
		// 0000 0000 0100 0000 0000 0000 0000 0000
		payloadUnitInd = (b & 0x00400000) > 0;
		
		// 0000 0000 0010 0000 0000 0000 0000 0000
		transpPriority = (b & 0x00200000) > 0;
		
		// 0000 0000 0001 1111 1111 1111 0000 0000
		pid = b & 0x001FFF00;
		
		// 0000 0000 0000 0000 0000 0000 1100 0000
		transpScrCtl = b & 0x000000C0;
		
		// 0000 0000 0000 0000 0000 0000 0011 0000
		adapFieldCtl = b & 0x00000030;
		
		// 0000 0000 0000 0000 0000 0000 0000 1111
		contCount = b & 0x0000000F;
		
		return this;
	}

	/* getters */
	
	public int getSyncByte() {
		return syncByte;
	}
	
	public boolean isTranspErrInd() {
		return transpErrInd;
	}
	
	public boolean isPayloadUnitInd() {
		return payloadUnitInd;
	}
	
	public boolean isTranspPriority() {
		return transpPriority;
	}
	
	public int getPID() {
		return pid;
	}
	
	public int getTranspScrCrl() {
		return transpScrCtl;
	}
	
	public int getAdapFieldCtl() {
		return adapFieldCtl;
	}
	
	public int getContCount() {
		return contCount;
	}
	
	
	/* setters */
	
	public void setSyncByte(int syncByte) {
		this.syncByte = syncByte;
	}
	
	public void setTranspErrInd(boolean transpErrInd) {
		this.transpErrInd = transpErrInd;
	}
	
	public void setPayloadUnitInd(boolean payloadUnitInd) {
		this.payloadUnitInd = payloadUnitInd;
	}
	
	public void setTranspPriority(boolean transpPriority) {
		this.transpPriority = transpPriority;
	}
	
	public void setPID(int pid) {
		this.pid = pid;
	}
	
	public void setTranspScrCtl(int transpScrCtl) {
		this.transpScrCtl = transpScrCtl;
	}
	
	public void setAdapFieldCtl(int adapFieldCtl) {
		this.adapFieldCtl = adapFieldCtl;
	}
	
	public void setContCount(int contCount) {
		this.contCount = contCount;
	}
}


package net.floodlightcontroller.packet;

public enum HTTPMethod {
	
	/*
	 * RFC - 7231
	 * https://tools.ietf.org/html/rfc7231
	 */
	
	GET(1, "GET"),
	HEAD(2, "HEAD"),
	POST(3, "POST"),
	PUT(4, "PUT"),
	DELETE(5, "DELETE"),
	CONNECT(6, "CONNCT"),
	OPTIONS(7, "OPTIONS"),
	TRACE(8, "TRACE"),
	NONE(9, "NOT HTTP METHOD");
	
	protected int type;
	protected String label;
	
	private HTTPMethod(int type, String label) {
		this.type = type;
		this.label = label;
	}
	
	public static HTTPMethod getType(int value) {
		switch(value) {
			case 0: 
				return GET;
			case 1:
				return HEAD;
			case 2:
				return POST;
			case 3:
				return PUT;
			case 4:
				return DELETE;
			case 5:
				return CONNECT;
			case 6:
				return OPTIONS;
			case 7:
				return TRACE;
			default:
				return NONE;
		}
	}
	
	public String getLabel() {
		return this.label;
	}
	
	public int getIntType() {
		return this.type;
	}
}


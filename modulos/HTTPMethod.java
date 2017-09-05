package net.floodlightcontroller.packet;

public enum HTTPMethod {
	
	/*
	 * Author: Nadyan Suriel Pscheidt
	 * 
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
	
	public static HTTPMethod getType(String value) {
		switch(value) {
			case "GET": 
				return GET;
			case "HEAD":
				return HEAD;
			case "POST":
				return POST;
			case "PUT":
				return PUT;
			case "DELETE":
				return DELETE;
			case "CONNECT":
				return CONNECT;
			case "OPTIONS":
				return OPTIONS;
			case "TRACE":
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

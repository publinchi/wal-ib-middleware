package com.cobiscorp.channels.atm.util;

import java.nio.ByteBuffer;

public class BaseTcpIpProvider {
	public BaseTcpIpProvider(){
		tcpIpHeader = 5;
	}
	public BaseTcpIpProvider(int aTcpIpHeader){
		tcpIpHeader = aTcpIpHeader;
	}
	
	public int getTcpIpHeader() {
		return tcpIpHeader;
	}

	/**
	 * represents header for provider it is up of the tcp-handler
	 */
	private int tcpIpHeader;
	
	protected short convertntohs(byte[] value) {
		ByteBuffer buf = ByteBuffer.wrap(value);
		return buf.getShort();
	}	
	protected byte[] converthtons(short sValue) {
		byte[] baValue = new byte[2];
		ByteBuffer buf = ByteBuffer.wrap(baValue);
		return buf.putShort(sValue).array();
	}
	

}

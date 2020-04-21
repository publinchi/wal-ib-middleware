package com.cobiscorp.channels.atm.util;

import java.io.IOException;
import java.io.OutputStream;

class Sender extends BaseTcpIpProvider {

	private OutputStream outputStream;

	public Sender(){
		super();
	}
	public Sender(int aTcpIpHeader){
		super(aTcpIpHeader);
	}
	
	public synchronized void send(String outFrame) {
		try {
			//Obtiene y envia la longitud del mensaje
			short len = (short) (outFrame.length());
			System.out.println("length to write -> " + len);
			byte[] lengthBytes = converthtons(len);
			System.out.println("sending bytes of length...");
			outputStream.write(lengthBytes);
			if(getTcpIpHeader() == 5){
				// Envia 2 bytes en 0 (Aplica solo para header 5)
				System.out.println("sending 00 bytes..");
				outputStream.write(0);
				outputStream.write(0);
			}
			// Envia los bytes de datos
			System.out.println("sending bytes of data..");
			outputStream.write(outFrame.getBytes());			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}
}

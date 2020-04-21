package com.cobiscorp.channels.atm.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

import com.cobiscorp.cis.connector.model.BuiltTransaction;
import com.cobiscorp.cis.connector.model.Connector;



public class Receiver extends BaseTcpIpProvider implements Runnable  {
	ConcurrentMap<String, BlockingQueue<ProviderItem>> responseFrames;	
	private InputStream inputStream;
	Connector connector;
	String correlationField;

	public Receiver(ConcurrentMap<String, BlockingQueue<ProviderItem>> aResponseFrames,
			Connector aConnector, String aCorrelationField){
		responseFrames = aResponseFrames;
		connector = aConnector;
		correlationField = aCorrelationField;
		
	}
	
	@Override
	public void run() {
		try {
			while(true) {
				byte lengthBytes[] = new byte[2];
				int nBytesReaded = inputStream.read(lengthBytes);
				System.out.println("Message received, processing ...");
				System.out.println("Number of bytes readed -> " + nBytesReaded);
				short len = convertntohs(lengthBytes);
				System.out.println("length -> " + len);
				
				if(getTcpIpHeader() == 5){
					// Lee y descarta 2 bytes que vienen en 0 (Aplica solo para header 5)
					System.out.println("Discarted byte -> " + inputStream.read());
					System.out.println("Discarted byte -> " + inputStream.read());
				}
				
				System.out.println("Reading bytes of data..");
				byte dataBytes[] = new byte[len];
				nBytesReaded = inputStream.read(dataBytes, 0, len);
				System.out.println("Number of bytes readed -> " + nBytesReaded);
				byte readedByte[] = Arrays.copyOf(dataBytes, len);
				BuiltTransaction wBuildTransaction = getCorrelation(readedByte);
				ProviderItem wProviderItem = new ProviderItem();
				wProviderItem.key = wBuildTransaction.getFieldValues().get(correlationField).getValue();
				wProviderItem.builtTransaction = wBuildTransaction;
				
				System.out.println("using correlId:"+ wProviderItem.key);
				//get queue for the request
				responseFrames.get(wProviderItem.key).add(wProviderItem);
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	protected BuiltTransaction getCorrelation(byte[] aFrame) {
		ByteBuffer wRequestBuffer = ByteBuffer.allocate(aFrame.length);
		wRequestBuffer.put(aFrame);
		// connector.parseMessage(tramaIso);
		BuiltTransaction builtTransaction = connector.parseFromMessage(wRequestBuffer);
		return builtTransaction;
		
	}	
}
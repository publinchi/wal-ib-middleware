package com.cobiscorp.channels.atm.util;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.jboss.netty.util.internal.LinkedTransferQueue;

import com.cobiscorp.cis.connector.model.BuiltTransaction;
import com.cobiscorp.cis.connector.model.Connector;

/**
 * External Provider Simulator
 * @author cinapanta
 *
 */
public class Provider {


	private Socket endPoint;
	private static Sender sender;
	protected ConcurrentMap<String, BlockingQueue<ProviderItem>> responseFrames;

	
	public Provider(String host, int port, 
			Connector aConnector, String aCorrelationField, 
			Integer aTcpIpHeader) {
		
		responseFrames = new ConcurrentHashMap<String, BlockingQueue<ProviderItem>>();
		

		try {
			System.out.println("connecting to server " + host + ":" + port + "...");
			endPoint = new Socket(host, port);
			if(endPoint != null) {
				System.out.println("connected -> " + endPoint);
			}
			
			sender = new Sender(aTcpIpHeader);
			sender.setOutputStream(endPoint.getOutputStream());
			
			System.out.println("initializing and starting receiver...");

			Receiver receiver = new Receiver(responseFrames, aConnector, aCorrelationField);
		    Thread t1 = new Thread(receiver);
		    


			receiver.setInputStream(endPoint.getInputStream());
			t1.start();			
			
			System.out.println("=============================================");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	

	public BuiltTransaction sendFrame(String aFrame, String aKey){
		System.out.println("send with key:"+ aKey);
		LinkedTransferQueue<ProviderItem> wQueue = new LinkedTransferQueue<ProviderItem>(); 
		responseFrames.put(aKey, wQueue);
		
		sender.send(aFrame);

		ProviderItem wProviderItem = null;
		try {
			System.out.println("waiting from queue with key:"+ aKey);
			wProviderItem = wQueue.take();
			System.out.println("remove queue with key:"+ aKey);
			responseFrames.remove(aKey);
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return wProviderItem.builtTransaction;
	}	
}

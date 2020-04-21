package com.cobiscorp.channels.atm.test;

import java.math.BigDecimal;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Sample to sincronize threads
 * @author fabad
 *
 */
public class Setup {
	static final int SIZE = 10;
	
	
	public static void main(String[] args) {
		BigDecimal w= new BigDecimal("99990200.25");
		w = w.multiply(new BigDecimal("100"));
		System.out.println("DD" + w);
		String p4Transac = String.format("%012d", w.longValue());
		System.out.println("DD" + p4Transac);
		
	}
	/**
	 * @param args
	 */
	public static void main1(String[] args) {
		 
		
		ConcurrentMap<String, BlockingQueue<KeyItem>> responseFrames = new ConcurrentHashMap<String, BlockingQueue<KeyItem>>();
		Producer p = new Producer(responseFrames);
		new Thread(p).start();
		Consumer[] consumers = new Consumer[SIZE];
		for(int i=0;i<SIZE;i++){
			String aKey = ""+ i;
			//responseFrames.put(aKey, new LinkedTransferQueue<KeyItem>());
			consumers[i] = new Consumer(responseFrames, aKey);
			new Thread(consumers[i]).start();
		}
		
		
		/*Consumer c0 = new Consumer(responseFrames, "0");
		Consumer c1 = new Consumer(responseFrames, "1");
		Consumer c2 = new Consumer(responseFrames, "2");
		Consumer c3 = new Consumer(responseFrames, "2");
		
		
		new Thread(c0).start();
		new Thread(c1).start();
		new Thread(c2).start();*/
	}

}

class Producer implements Runnable {
	// private final BlockingQueue<KeyItem> queue;
	protected ConcurrentMap<String, BlockingQueue<KeyItem>> responseFrames;

	/*
	 * Producer(BlockingQueue<KeyItem> q) { queue = q; }
	 */

	public void run() {
		try {
			while (true) {
				produce();
				Thread.sleep(1000);
			}
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}

	public Producer(ConcurrentMap<String, BlockingQueue<KeyItem>> responseFrames) {

		this.responseFrames = responseFrames;
	}

	// String produce() { return "hola" + Thread.currentThread().getName(); }
	void produce() throws InterruptedException {
		KeyItem wKeyItem = new KeyItem();
		wKeyItem.key = ""
				+ new java.util.Random(System.currentTimeMillis()).nextInt(Setup.SIZE);
		wKeyItem.value = "hola: " + wKeyItem.key + ":" + Thread.currentThread().getName();
		
		if(responseFrames.get(wKeyItem.key) != null){
			responseFrames.get(wKeyItem.key).add(wKeyItem);
			System.out.println("produce for:" + wKeyItem.key);
		}
		

	}
}

class Consumer implements Runnable {

	private final BlockingQueue<KeyItem> queue;
	ConcurrentMap<String, BlockingQueue<KeyItem>> responseFrames;
	private String key;

	Consumer(ConcurrentMap<String, BlockingQueue<KeyItem>> aResponseFrames, String aKey) {
		responseFrames = aResponseFrames;
		queue = responseFrames.get(aKey);
		key = aKey;
	}

	public void run() {
		try {
			//while (true) {
				consume(queue.take());
				responseFrames.remove(key);
			//}
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}

	void consume(KeyItem x) {

		System.out.println("consumí desde: "+  Thread.currentThread().getName() +  ":" + x.value);

	}
}

class KeyItem {
	public String key;
	public String value;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KeyItem other = (KeyItem) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}

}

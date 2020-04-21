package com.cobiscorp.channels.atm.test;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
/**
 * Sample to sincronize threads
 * @author fabad
 *
 */
public class DelayQueueTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DelayQueue  dq=new DelayQueue();
		DeleyedTest ob1=new DeleyedTest(10000);
		DeleyedTest ob2=new DeleyedTest(5000);
		DeleyedTest ob3=new DeleyedTest(15000);
		
		dq.offer(ob1);
		dq.offer(ob2);
		dq.offer(ob3);
		
		//Iterator itr=dq.iterator();
		//while(itr.hasNext()){
		while(!dq.isEmpty()){
			
			DeleyedTest dt=(DeleyedTest)dq.poll();
			if(dt!=null){
				//DeleyedTest dt=(DeleyedTest)itr.next();
				System.out.println(dt.adeleyTime);
			}else {
				//System.out.println("getting");
			}
		}

	}

}

class DeleyedTest implements Delayed{
    public long deleyTime=0;
    public long adeleyTime=0;
    DeleyedTest(long deleyTime){
    	this.adeleyTime = deleyTime;
    	this.deleyTime=deleyTime + System.currentTimeMillis();
    }
    
	@Override
	public int compareTo(Delayed ob) {
		if(this.deleyTime<((DeleyedTest)ob).deleyTime){
			return -1;
		}else if(this.deleyTime>((DeleyedTest)ob).deleyTime){
			return 1;
		}
		return 0;
	}
	@Override
	public long getDelay(TimeUnit unit) {
		//System.out.println("deleyTime" + deleyTime);
		//System.out.println("currentTimeMillis" + System.currentTimeMillis());
		//System.out.println("diff" + (deleyTime-System.currentTimeMillis()));
	    //long diff = deleyTime - System.currentTimeMillis();
	    //return unit.convert(diff, TimeUnit.MILLISECONDS);
		return deleyTime - System.currentTimeMillis();
		
	}
	
}
package com.amazonaws.samples;

public class LoadASXData implements Runnable{

	public void run(){
		AsxGame.asxLoadComplete = false;
		
		long startTime = System.currentTimeMillis();		
		Thread t1 = new Thread(new AsxPullThread(0,250));
		Thread t2 = new Thread(new AsxPullThread(250, 500));
		Thread t3 = new Thread(new AsxPullThread(500, 750));
		Thread t4 = new Thread(new AsxPullThread(750, 1000));
		Thread t5 = new Thread(new AsxPullThread(1000, 1250));
		Thread t6 = new Thread(new AsxPullThread(1250, 1500));
		Thread t7 = new Thread(new AsxPullThread(1500, 1750));
		Thread t8 = new Thread(new AsxPullThread(1750, -1));
		t1.start();
		t2.start();
		t3.start();
		t4.start();
		t5.start();
		t6.start();
		t7.start();
		t8.start();
		try{														//uncomment these bits to time asx pull stuff
			t1.join();
			t2.join();
			t3.join();
			t4.join();
			t5.join();
			t6.join();
			t7.join();
			t8.join();
			AsxGame.asxLoadComplete = true;
		} catch (InterruptedException e){
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();
		long runTime = (endTime - startTime) / 1000;
		System.out.println(Long.toString(runTime));
		
	}
}

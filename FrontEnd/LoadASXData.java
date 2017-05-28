/*
	This class is a Thread that itself runs the threads to download the Asx Data from the s3
	bucket. includes a timer for testing purposes
	
 */

package com.amazonaws.samples;

import javafx.application.Platform;

public class LoadASXData implements Runnable{

	public void run(){
		AsxGame.asxLoadComplete = false;
		
		//sets load percentage to 0 before starting loads
		AsxGame.loadCompletePercent = 0;
		
		long startTime = System.currentTimeMillis();		
		Thread t1 = new Thread(new AsxPullThread(0,250));
		Thread t2 = new Thread(new AsxPullThread(250, 500));
		Thread t3 = new Thread(new AsxPullThread(500, 750));
		Thread t4 = new Thread(new AsxPullThread(750, 1000));
		Thread t5 = new Thread(new AsxPullThread(1000, 1250));
		Thread t6 = new Thread(new AsxPullThread(1250, 1500));
		Thread t7 = new Thread(new AsxPullThread(1500, 1750));
		Thread t8 = new Thread(new AsxPullThread(1750, 2000));
		Thread t9 = new Thread(new AsxPullThread(2000, -1));
		t1.start();
		t2.start();
		t3.start();
		t4.start();
		t5.start();
		t6.start();
		t7.start();
		t8.start();
		t9.start();
		try{														//uncomment these bits to time asx pull stuff
			t1.join();
			t2.join();
			t3.join();
			t4.join();
			t5.join();
			t6.join();
			t7.join();
			t8.join();
			t9.join();
		} catch (InterruptedException e){
			e.printStackTrace();
		}
		//Implement bubble sort on ASX Stock List
		long endTime = System.currentTimeMillis();
		long runTime = (endTime - startTime) / 1000;
		System.out.println("Pull Time (s): " + Long.toString(runTime));
		
		startTime = System.currentTimeMillis();
		bubbleSortASX();
		endTime = System.currentTimeMillis();
		runTime = (endTime - startTime) / 1000;
		System.out.println("Sort Time (s): " + Long.toString(runTime));
		
		//ignore these, they are for UI usage, will likely disappear once, JavaFX implemented
		AsxGame.asxLoadComplete = true;
		if (AsxGame.showUI){
			Platform.runLater(new Runnable() {
				@Override
				public void run(){
					if (!AsxGame.asxLoadComplete) {
						AsxGame.mainStage.setTitle("ASX \"Trading Wheels\" - " + AsxGame.loadCompletePercent);
					} else {
						// update UI stuff after download completes
						AsxGame.mainStage.setTitle("ASX \"Trading Wheels\"");
						
						UI_BrowseStockWindow.initBrowseStockWindow();
						if (UI_MainScene.browseWindowVis){
							UI_MainScene.homeScreenStack.getChildren().remove(1);
							UI_BrowseStockWindow.makeBrowseStockWindow();
						}
						if (AsxGame.UI_MainScene != null){
							UI_Portfolio.updatePortfolioTable();
						}
					}
				}
			});
		}
	}
	
	private void bubbleSortASX()
	{
		Stock stock;
		Stock nextStock;
		for(int i=1; i<AsxGame.stockArray.size(); i++)
		{
			for(int j=0; j<AsxGame.stockArray.size()-i; j++)
			{
				stock = AsxGame.stockArray.get(j);
				nextStock = AsxGame.stockArray.get(j+1);
				if(stock.code.compareTo(nextStock.code) > 0) //yields true if alphabetically nextStock should be before stock
				{
					//swap positions of stock and nextStock within arraylist
					AsxGame.stockArray.set(j, nextStock);
					AsxGame.stockArray.set(j+1, stock);
				}
			}
		}
	}
}

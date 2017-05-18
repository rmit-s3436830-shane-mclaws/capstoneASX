package com.amazonaws.samples;

import java.util.ArrayList;

import javafx.application.Platform;

public class MessageCheck implements Runnable
{
	public void run()
	{
		while(AsxGame.activePlayerLoaded)
		{
			try
			{
				Thread.sleep(60000); //Waits 1 minute before updating lists
				ArrayList<Integer> newMesList = Game.getMessageList();
				if(!newMesList.equals(AsxGame.activePlayer.messages))
				{
					System.out.println("YOU HAVE MAIL!");
					AsxGame.activePlayer.unreadMessages = Game.getUnreadMessages();
				}
				AsxGame.activePlayer.messages = newMesList;
				AsxGame.activePlayer.deletedMessages = Game.getDeletedMessageList();
				ArrayList<Integer> newFundList = Game.getFundsList();
				if(!newFundList.equals(AsxGame.activePlayer.pendingFunds))
				{
					System.out.println("YOU HAVE NEW PENDING FUNDS TRANSFERS!");
				}
				AsxGame.activePlayer.pendingFunds = newFundList;
			}
			catch (InterruptedException e)
			{
				return;
			}
			Platform.runLater(new Runnable() {
				@Override
				public void run(){
					UI_MainScene.updateUnreadMessages();
				}
			});
		}
		while(AsxGame.activeAdminLoaded)
		{
			try
			{
				Thread.sleep(60000); //Waits 1 minute before updating lists
				ArrayList<Integer> newMesList = Game.getMessageList();
				if(!newMesList.equals(AsxGame.activeAdmin.messages))
				{
					System.out.println("YOU HAVE MAIL!");
					AsxGame.activeAdmin.unreadMessages = Game.getUnreadMessages();
				}
				AsxGame.activeAdmin.messages = newMesList;
				AsxGame.activeAdmin.deletedMessages = Game.getDeletedMessageList();
				ArrayList<Integer> newFundList = Game.getFundsList();
				if(!newFundList.equals(AsxGame.activeAdmin.pendingFunds))
				{
					System.out.println("YOU HAVE NEW PENDING FUNDS TRANSFERS!");
				}
				AsxGame.activeAdmin.pendingFunds = newFundList;
			}
			catch (InterruptedException e)
			{
				return;
			}
		}
	}
}

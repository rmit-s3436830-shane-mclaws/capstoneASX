package com.amazonaws.samples;
//Prototype implements acknowledgments on messages and implements a user save function/call

/************************************************************************
* This script will manage user accounts for the ASX trading game.      	*
* This server will be called when the user attempts to login, register	*
* save their profile, or view the leader board. Incoming connections are*
* handled by threads, which are handled in the threadedConnection class	*
************************************************************************/
import java.io.*;
import java.net.*;
import java.util.*;

public class UserServer
{
	public static void main(String args[]) throws SocketException
	{
		System.out.println("========================STARTING SERVER========================");
		Enumeration<NetworkInterface> iNets = NetworkInterface.getNetworkInterfaces();
		System.out.println("________________________Network Information________________________");
        for(NetworkInterface iNet : Collections.list(iNets))
        {
        	System.out.printf("Display Name: %s\n",iNet.getDisplayName());
			Enumeration<InetAddress> addrs = iNet.getInetAddresses();
			for(InetAddress addr : Collections.list(addrs))
			{
				System.out.printf("\tLocal IP Address: %s\n",addr.getHostAddress());
			}
			
        }
        System.out.println("----------------------End Network Information----------------------\n\n");
        
		ServerSocket serverSock = null;
		Socket userConnection = null;
		try
		{
			serverSock = new ServerSocket(28543);
			serverSock.setReuseAddress(true);
			while(true)
			{
				//Open user connection in new thread
				//But keep server socket open to accept additional connections
				userConnection = serverSock.accept();
				Thread conn = new Thread(new threadedConnection(userConnection));
				conn.start();
			}
		}
		catch (SocketException se)
		{
			System.out.println("Exception while performing socket operation: " + se);
		}
		catch(IOException e)
		{
			System.out.println("Exception while opening server socket: " + e);
		}
		finally
		{
			try
			{
				serverSock.close();
			}
			catch(IOException e)
			{
				System.out.println("Exception while closing socket: " + e);
			}
		}
	}
}

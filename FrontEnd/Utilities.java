/*
	generic Utility class/functions
	currently only contais code for writing errors to log files
	
 */

package com.amazonaws.samples;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.Socket;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class Utilities {
	
	public static void errorToLogFile(String error){
		try{
			Writer writer;
			writer = new BufferedWriter(new FileWriter("errorLog.log", true));
			writer.append(error + "\n");
			writer.close();
			return;
		} catch (IOException e){
			e.printStackTrace();			
			return;
		}
	}
	public static void asxErrorToLogFile(String file, String error){
		try{
			Writer writer;
			writer = new BufferedWriter(new FileWriter("asxErrorLog.log", true));
			writer.append(file + ": " + error + "\n");
			writer.close();
			return;
		} catch (IOException e){
			e.printStackTrace();			
			return;
		}
	}
	
	public static String sendServerMessage(String message)
	{
		Socket connection = null;
		String returnData = "";
		try
		{
			connection = new Socket(AsxGame.connectionName, AsxGame.portNumber);
			
			//Input Streams
			BufferedReader connectionRead = null;
			String response = null;
			
			//Output Streams
			DeflaterOutputStream deflStream = null;
			
			try
			{
				//Call
				deflStream = new DeflaterOutputStream(connection.getOutputStream(), true);
				byte[] sendBytes = message.getBytes("UTF-8");
				deflStream.write(sendBytes);
				deflStream.finish();
				deflStream.flush();
				
				while (true)
				{
					connectionRead = new BufferedReader(new InputStreamReader(new InflaterInputStream(connection.getInputStream())));
					response = connectionRead.readLine();
					if(response != null)
					{
						returnData += response + "\n";
						while((response = connectionRead.readLine()) != null)
						{
							returnData += response + "\n";
						}
						break;
					}
				}
				
			}
			catch (IOException e)
			{
				System.out.println("Exception while reading connection response: " + e);
				return null;
			}
		}
		catch (IOException e)
		{
			System.out.println("Exception while opening connection: " + e);
			return null;
		}
		try
		{
			connection.close();
		}
		catch (IOException e)
		{
			System.out.println("Exception while closing connection: " + e);
		}
		return returnData;
	}
}

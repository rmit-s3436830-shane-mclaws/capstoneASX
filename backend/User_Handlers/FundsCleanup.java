package com.amazonaws.samples;

import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import org.json.*;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class FundsCleanup
{
	private static String AccessKey = creds.accessKey;
	private static String SecretKey = creds.secretAccessKey;
	private static String bucket = "asx-user-store";
	private static AWSCredentials credentials;
	private static AmazonS3 s3Client;
	private static String date; 
	
	public static void main(String args[])
	{
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		df.setTimeZone(TimeZone.getTimeZone("Australia/Victoria"));
		Date dateobj = new Date();
		FundsCleanup.date = df.format(dateobj);
		
		credentials = new BasicAWSCredentials(FundsCleanup.AccessKey,FundsCleanup.SecretKey);
		s3Client  = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(Regions.AP_SOUTHEAST_2).build();
		
		//Generate List of users
		List<Integer> users = new ArrayList<Integer>();
		String prefix = "data/";
		ObjectListing objectList = s3Client.listObjects(bucket, prefix);
		List<S3ObjectSummary> summaries = objectList.getObjectSummaries();
		summaries.remove(0);
		if(summaries.size() != 0)
		{
			if(objectList.isTruncated())
			{
				do
				{
					for(S3ObjectSummary summary : summaries)
					{
						int ID = Integer.parseInt(summary.getKey().split("/")[1]);
						if(users.isEmpty())
						{
							users.add(ID);
						}
						else if(users.get(users.size() - 1) != ID)
						{
							users.add(ID);
						}
					}
					objectList = s3Client.listNextBatchOfObjects(objectList);
				}while (objectList.isTruncated());
			}
			else
			{
				for(S3ObjectSummary summary : summaries)
				{
					int ID = Integer.parseInt(summary.getKey().split("/")[1]);
					if(users.isEmpty())
					{
						users.add(ID);
					}
					else if(users.get(users.size() - 1) != ID)
					{
						users.add(ID);
					}
				}
			}
		}
		
		for(int user : users)
		{
			checkMessages(user);
		}
	}
	
	private static void checkMessages(int userID)
	{
		//Generate list of funds objects for user
		String prefix = "data/" + userID + "/mailbox/";
		ObjectListing objectList = s3Client.listObjects(bucket, prefix);
		List<S3ObjectSummary> summaries = objectList.getObjectSummaries();
		if(summaries.size() != 0)
		{
			if(objectList.isTruncated())
			{
				do
				{
					for(S3ObjectSummary summary : summaries)
					{
						String key = summary.getKey();
						processMessage(key);
					}
					objectList = s3Client.listNextBatchOfObjects(objectList);
				}while (objectList.isTruncated());
			}
			else
			{
				for(S3ObjectSummary summary : summaries)
				{
					String key = summary.getKey();
					processMessage(key);
				}
			}
		}
		
	}
	
	private static void processMessage(String key)
	{
		S3Object object = s3Client.getObject(new GetObjectRequest(bucket, key));
		InputStream objectData = object.getObjectContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(objectData));
		String line = "";
		try
		{
			line = reader.readLine();
			JSONObject mailJSON = new JSONObject(line);
			int iDate = Integer.parseInt(FundsCleanup.date);
			if(mailJSON.getString("Type").equals("funds"))
			{
				if(iDate - mailJSON.getInt("Date") > 5)
				{
					//Establish connection to server service to update user details
					Socket connection = new Socket("127.0.0.1", 28543);
					//Input Streams
					BufferedReader connectionRead = null;
					String response = null;
					
		    		//Output Streams
					DeflaterOutputStream deflStream = null;
					deflStream = new DeflaterOutputStream(connection.getOutputStream(), true);
					
					//get user email hash
					String userID = key.split("[/.]")[1];
					String dataFile = "data/" + userID + "/data.json";
					object = s3Client.getObject(new GetObjectRequest(bucket, dataFile));
					objectData = object.getObjectContent();
					reader = new BufferedReader(new InputStreamReader(objectData));
					line = reader.readLine();
					JSONObject userData = new JSONObject(line);
					String email = userData.getString("Email");
					String userHash = Integer.toString(email.hashCode());
					
					//get ID of funds being deleted
					String fundID = key.split("[/.]")[3];
					
					//send acceptance of 0 funds - delete request
					String sendString = "acceptFunds\n" + userHash + "\n" + fundID + "\n0";
					byte[] sendBytes = sendString.getBytes("UTF-8");
					deflStream.write(sendBytes);
					deflStream.finish();
					deflStream.flush();
					
					while(true)
					{
						connectionRead = new BufferedReader(new InputStreamReader(new InflaterInputStream(connection.getInputStream())));
						response = connectionRead.readLine();
						if(response != null)
						{
							if(response.equals("200"))
							{
								System.out.println("Funds successfully rejected!\n" + key);
								break;
							}
							else
							{
								System.out.println(key + "\n500: INTERNAL SERVER ERROR!");
								break;
							}
						}
					}
					connection.close();
				}
			}
		}
		catch(IOException ie)
		{
			System.out.println("Exception caught when reading: " + key);
			ie.printStackTrace();
		}
		finally
		{
			try
			{
				reader.close();
			}
			catch(IOException ie)
			{
				System.out.println("Exception caught when closing reader! Key: " + key);
				ie.printStackTrace();
			}
		}
		return;
	}
}

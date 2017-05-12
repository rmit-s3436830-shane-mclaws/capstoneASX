package com.amazonaws.samples;

import java.io.*;
import java.net.*;
import java.util.*;
import org.json.*;
import java.util.zip.InflaterInputStream;
import java.util.zip.DeflaterOutputStream;

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

public class scoreUpdate
{
	private static String AccessKey = creds.accessKey;
	private static String SecretKey = creds.secretAccessKey;
	
	public static void main(String args[])
	{
		AWSCredentials credentials;
		AmazonS3 s3Client;
		String bucket = "asx-user-store";
		List<Integer> users = new ArrayList<Integer>();
		
		credentials = new BasicAWSCredentials(scoreUpdate.AccessKey,scoreUpdate.SecretKey);
		s3Client  = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(Regions.AP_SOUTHEAST_2).build();
		
		ObjectListing objectList = s3Client.listObjects(bucket, "data/");
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
			//call function passing in user number that updates score
			updateScore(user);
		}
	}
	
	private static void updateScore(int user)
	{
		Socket connection = null;
		AWSCredentials credentials = new BasicAWSCredentials(scoreUpdate.AccessKey,scoreUpdate.SecretKey);
		AmazonS3 s3Client  = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(Regions.AP_SOUTHEAST_2).build();
		
		String bucket = "asx-user-store";
		String userData = "data/" + user + "/data.json";
		
		try
		{
			float newScore = 0;
			final float startingCash = 1000000;
			//Get contents of user file
			S3Object object = s3Client.getObject(new GetObjectRequest(bucket, userData));
			InputStream objectData = object.getObjectContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(objectData));
			object = s3Client.getObject(new GetObjectRequest(bucket, userData));
			objectData = object.getObjectContent();
			reader = new BufferedReader(new InputStreamReader(objectData));
			String data = reader.readLine();
			reader.close();
			objectData.close();
			JSONObject dataJSON = new JSONObject(data);
			
			//Get list of ASX codes
			String shares = dataJSON.get("Shares").toString();
			String userEmail = dataJSON.getString("Email").toString();
			String emailHash = Integer.toString(userEmail.hashCode());
			String[] sharesArray = shares.split("[,:]");//even values hold ASX code, odd values hold qty
			
			if(!shares.equals("")) //If user owns shares
			{
				//Establish connection to server service to update user details
				connection = new Socket("127.0.0.1", 28543);
				//Input Streams
				BufferedReader connectionRead = null;
				String response = null;
				
	    		//Output Streams
				DeflaterOutputStream deflStream = null;
				deflStream = new DeflaterOutputStream(connection.getOutputStream(), true);
				
				//for each ASX code, get value of shares owned
				for(int i=0; i<sharesArray.length; i+=2)
				{
					newScore += getASXValue(sharesArray[i].replaceAll("\"", ""), Integer.parseInt(sharesArray[i+1].replaceAll("\"", "")));
				}
				
				//Add user balance to newScore
				String sBal = dataJSON.getString("Balance").toString();
				float bal = Float.parseFloat(sBal);
				newScore += bal;
				
				//Subtract 1000000 from newScore and if newScore < 0, set it to 0
				newScore -= startingCash;
				if(newScore < 0)
				{
					newScore = 0;
				}
				String stringScoreUpdated = Float.toString(newScore);
				connectionRead = new BufferedReader(new InputStreamReader(new InflaterInputStream(connection.getInputStream())));
				
				//Generate new dataJSON
				dataJSON.put("Score", stringScoreUpdated);
				
				//change score value to newScore
				String saveString = "save\n" + emailHash + "\n" + dataJSON.toString();
				byte[] saveBytes = saveString.getBytes("UTF-8");
				deflStream.write(saveBytes);
				deflStream.finish();
				deflStream.flush();
				while(true)
				{
					//System.out.println("Waiting for response");
					connectionRead = new BufferedReader(new InputStreamReader(new InflaterInputStream(connection.getInputStream())));
					response = connectionRead.readLine();
					if(response != null)
					{
						if(response.equals("200"))
						{
							System.out.println(userEmail + ": Save successful!\n");
							break;
						}
						else
						{
							System.out.println(userEmail + "500: INTERNAL SERVER ERROR!");
							break;
						}
					}
				}
	        	try
	        	{
	        		connection.close();
	        	}
	        	catch (IOException e)
	        	{
	        		System.out.println("Exception while closing connection: " + e);
	        	}
    		}
		}
		catch (IOException e)
		{
			System.out.println("Exception while reading input: " + e);
		}
	}
	
	static float getASXValue(String ASXCode, int qty)
	{
		//System.out.println("Code: " + ASXCode + " Qty: " + qty);
		try
		{
			AWSCredentials credentials;
			AmazonS3 s3Client;
			String bucket = "asx-json-host";
			
			credentials = new BasicAWSCredentials(scoreUpdate.AccessKey,scoreUpdate.SecretKey);
			s3Client  = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(Regions.AP_SOUTHEAST_2).build();
			ObjectListing objectList = s3Client.listObjects(bucket, ASXCode+"/");
			List<S3ObjectSummary> summaries = objectList.getObjectSummaries();
			
			S3ObjectSummary data = summaries.get(summaries.size() - 1);
			String dataKey = data.getKey();
			
			S3Object object = s3Client.getObject(new GetObjectRequest(bucket, dataKey));
			InputStream objectData = object.getObjectContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(objectData));
			
			List<String> dataEntries = new ArrayList<String>();
			String line = "";
			while((line = reader.readLine()) != null)
			{
				dataEntries.add(line);
			}
			String latestData = dataEntries.get(dataEntries.size() - 1);
			JSONObject jsonData = new JSONObject(latestData);
			String sValue = jsonData.getString("Last Trade Price");
			float value = Float.parseFloat(sValue);
			return value*qty;
		}
		catch (IOException ex)
		{
			System.out.println("Exception caught while read ASX data: " + ex);
			return -1;
		}
	}
}

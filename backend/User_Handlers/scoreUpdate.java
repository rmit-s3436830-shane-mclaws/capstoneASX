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
	public static void main(String args[])
	{
		AWSCredentials credentials;
		AmazonS3 s3Client;
		String bucket = "asx-user-store";
		List<Integer> users = new ArrayList<Integer>();
		int count = 0;
		
		credentials = new BasicAWSCredentials(REDACTED);
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
						if(count % 3 == 0)
						{
							users.add(Integer.parseInt(summary.getKey().split("/")[1]));
						}
						count++;
					}
					objectList = s3Client.listNextBatchOfObjects(objectList);
				}while (objectList.isTruncated());
			}
			else
			{
				for(S3ObjectSummary summary : summaries)
				{
					if(count % 3 == 0)
					{
						users.add(Integer.parseInt(summary.getKey().split("/")[1]));
					}
					count++;
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
		try
		{
			AWSCredentials credentials = new BasicAWSCredentials("AKIAJCT2NZ4EY44FKQMQ","NSUeEIkeThVVsRBEn4/cg/fV9h9pDgdpFgTMzf17");
			AmazonS3 s3Client  = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(Regions.AP_SOUTHEAST_2).build();
			
			String bucket = "asx-user-store";
			String userData = "data/" + user + "/data.json";
			
			connection = new Socket("127.0.0.1", 28543);
			/*System.out.println("Local Address: " + connection.getLocalAddress());
			System.out.println("Local Port: " + connection.getLocalPort());*/

			//Input Streams
			BufferedReader connectionRead = null;
			String response = null;
			
    		//Output Streams
			DeflaterOutputStream deflStream = null;
			deflStream = new DeflaterOutputStream(connection.getOutputStream(), true);
			
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
				
				//for each ASX code, get value of shares owned
				for(int i=0; i<sharesArray.length; i+=2)
				{
					newScore += getASXValue(sharesArray[i].replaceAll("\"", ""), Integer.parseInt(sharesArray[i+1].replaceAll("\"", "")));
				}
				
				//Add user balance to newScore
				String sBal = dataJSON.getString("Balance").toString();
				float bal = Float.parseFloat(sBal);
				newScore += bal;
				
				//Subtract 1000000 from newScore
				newScore -= startingCash;
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
							System.out.println(userEmail + "Save successful!");
							break;
						}
						else
						{
							System.out.println(userEmail + "500: INTERNAL SERVER ERROR!");
							break;
						}
					}
				}
    		}
    		catch (IOException e)
    		{
    			System.out.println("Exception while reading input: " + e);
    		}
		}
		catch (IOException e)
        {
        	System.out.println("Exception while opening connection: " + e);
        }
        finally
        {
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
	
	static float getASXValue(String ASXCode, int qty)
	{
		//System.out.println("Code: " + ASXCode + " Qty: " + qty);
		try
		{
			AWSCredentials credentials;
			AmazonS3 s3Client;
			String bucket = "asx-json-host";
			
			credentials = new BasicAWSCredentials("AKIAJCT2NZ4EY44FKQMQ","NSUeEIkeThVVsRBEn4/cg/fV9h9pDgdpFgTMzf17");
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

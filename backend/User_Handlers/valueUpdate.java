package com.amazonaws.samples;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import org.json.*;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class valueUpdate
{
	private static String AccessKey = "AKIAJREU35S4RGRV4OWQ";
	private static String SecretKey = "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";
	
	public static void main(String args[])
	{
		AWSCredentials credentials;
		AmazonS3 s3Client;
		String bucket = "asx-user-store";
		List<Integer> users = new ArrayList<Integer>();
		
		credentials = new BasicAWSCredentials(valueUpdate.AccessKey,valueUpdate.SecretKey);
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
			updateValue(user);
		}
	}
	
	private static void updateValue(int user)
	{
		AWSCredentials credentials = new BasicAWSCredentials(valueUpdate.AccessKey,valueUpdate.SecretKey);
		AmazonS3 s3Client  = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(Regions.AP_SOUTHEAST_2).build();
		
		String bucket = "asx-user-store";
		String userData = "data/" + user + "/data.json";
		
		try
		{
			float newValue = 0;
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
			String[] sharesArray = shares.split("[,:]");//even values hold ASX code, odd values hold qty
			
			if(!shares.equals("")) //If user owns shares
			{
				//for each ASX code, get value of shares owned
				for(int i=0; i<sharesArray.length; i+=2)
				{
					newValue += getASXValue(sharesArray[i].replaceAll("\"", ""), Integer.parseInt(sharesArray[i+1].replaceAll("\"", "")));
				}
			}
			
			//Add user balance to newScore
			String sBal = dataJSON.getString("Balance").toString();
			float bal = Float.parseFloat(sBal);
			newValue += bal;
			String stringValueUpdated = Float.toString(newValue);
			System.out.println("User " + user + " New value: " + stringValueUpdated);
			
			//Generate JSON to go into valueHistory
			JSONObject valueJSON = new JSONObject();
			DateFormat df = new SimpleDateFormat("yyyyMMdd");
			df.setTimeZone(TimeZone.getTimeZone("Australia/Victoria"));
			Date dateobj = new Date();
			String date = df.format(dateobj);
			valueJSON.put("Date", date);
			valueJSON.put("Value", stringValueUpdated);
			String JSONString = valueJSON.toString();
			
			//Open value history
			String fullHistory = "";
			String userValueHistory = "data/" + user + "/valueHistory.json";
			object = s3Client.getObject(new GetObjectRequest(bucket, userValueHistory));
			objectData = object.getObjectContent();
			reader = new BufferedReader(new InputStreamReader(objectData));
			
			//append new data to end
			while((data = reader.readLine()) != null)
			{
				fullHistory += data + "\n";
			}
			fullHistory += JSONString;
			
			//re-upload history file
			byte[] contentAsBytes = fullHistory.getBytes("UTF-8");
			ByteArrayInputStream contentAsStream = new ByteArrayInputStream(contentAsBytes);
			ObjectMetadata md = new ObjectMetadata();
			md.setContentLength(contentAsBytes.length);
			s3Client.putObject(new PutObjectRequest(bucket, userValueHistory, contentAsStream, md));
			System.out.println("User " + user + " updated");
			
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
			
			credentials = new BasicAWSCredentials(valueUpdate.AccessKey,valueUpdate.SecretKey);
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

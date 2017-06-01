package com.amazonaws.samples;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.json.*;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public class downloader implements Runnable
{
	private String[] companyList;
	private AWSCredentials credentials;
	private static AmazonS3 s3Client;
	private static final String bucket = manager.bucket;
	private static String date;
	private static String time;
	private static String AccessKey = manager.AccessKey;
	private static String SecretKey = manager.SecretKey;
	
	public downloader(String[] companies)
	{
		this.companyList = companies;
		this.credentials = new BasicAWSCredentials(downloader.AccessKey,downloader.SecretKey);
		downloader.s3Client  = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(Regions.AP_SOUTHEAST_2).build();
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		DateFormat tf = new SimpleDateFormat("HH:mm");
		df.setTimeZone(TimeZone.getTimeZone("Australia/Victoria"));
		tf.setTimeZone(TimeZone.getTimeZone("Australia/Victoria"));
		Date dateobj = new Date();
		downloader.date = df.format(dateobj);
		downloader.time = tf.format(dateobj);
	}
	
	public void run()
	{
		//Define Variables
		String data;
		String dataString;
		String fileName;
		byte[] contentAsBytes;
		ByteArrayInputStream contentAsStream;
		ObjectMetadata md;
		S3Object object;
		InputStream objectData;
		BufferedReader s3Reader;
		String fullData = "";
		/*****************************************************/
		try
		{
			//For each company code this thread is handling...
			for(String code:companyList)
			{
				//Downloads data from Yahoo Finance
				if((dataString = downloadData(code)) != null) //Will return null if no data could be retrieved
				{
					JSONObject checkAlive = new JSONObject(dataString);
					if(!checkAlive.get("Name").toString().equals("N/A")) //If the company is still alive, then...
					{
						//Upload json object to amazon S3
						manager.stockCodes.add(code); //Add stock code to stock code list
						fileName = code + "/" + downloader.date + ".json";
						if(!s3Client.doesObjectExist(bucket, fileName))
						{
							//Create new data file for day
							contentAsBytes = dataString.getBytes("UTF-8");
							contentAsStream = new ByteArrayInputStream(contentAsBytes);
							md = new ObjectMetadata();
							md.setContentLength(contentAsBytes.length);
							s3Client.putObject(new PutObjectRequest(bucket, fileName, contentAsStream, md));
							//System.out.println("Code complete: " + code);
						}
						else
						{
							fullData = "";
							//Append data to end of existing file
							//System.out.println("Reading S3 file: " + fileName);
							object = s3Client.getObject(new GetObjectRequest(bucket, fileName));
							objectData = object.getObjectContent();
							s3Reader = new BufferedReader(new InputStreamReader(objectData));
							while((data = s3Reader.readLine()) != null)
							{
								fullData += data + "\n";
								//System.out.println(data);
							}
							fullData += dataString;
							//System.out.println("Full String: " + fullData);
							//Upload new data
							contentAsBytes = fullData.getBytes("UTF-8");
							contentAsStream = new ByteArrayInputStream(contentAsBytes);
							md = new ObjectMetadata();
							md.setContentLength(contentAsBytes.length);
							s3Client.putObject(new PutObjectRequest(bucket, fileName, contentAsStream, md));
							//System.out.println("Code complete: " + code);
						}
						/*****************************************************/
					}
				}
				
			}
			return;
		}
		catch (IOException ie)
		{
			System.out.println("Exception thrown when reading remote file!");
			ie.printStackTrace();
		}
	}
	
	public String downloadData(String code)
	{
		BufferedReader fileReader = null;
		String data = "";
		String dataString = "";
		try
		{
			//Get data file from yahoo finance using predefine tags to retrieve data points needed
			String tags = "nabl1t1c1p2ohgpwkjdqr1y";
			String dataURL = "https://download.finance.yahoo.com/d/quotes.csv?s=" + code + ".AX&f=" + tags;
			URL url = new URL(dataURL);
			fileReader = new BufferedReader(new InputStreamReader(url.openStream()));
			data = fileReader.readLine();
			fileReader.close();
			/*****************************************************/
			
			//Take retrieved data, and convert it to a json object
			String dataPoints[] = data.replaceAll("\"","").split(",");
			JSONObject dataJSON = new JSONObject();
			dataJSON.put("Time", downloader.time);
			dataJSON.put("Name", dataPoints[0]);
			dataJSON.put("ASX Code", code);
			dataJSON.put("Ask Price", dataPoints[1]);
			dataJSON.put("Bid Price", dataPoints[2]);
			dataJSON.put("Last Trade Price", dataPoints[3]);
			dataJSON.put("Last Trade Time", dataPoints[4]);
			dataJSON.put("Change", dataPoints[5]);
			dataJSON.put("Change(%)", dataPoints[6]);
			dataJSON.put("Opening Value", dataPoints[7]);
			dataJSON.put("Day High", dataPoints[8]);
			dataJSON.put("Day Low", dataPoints[9]);
			dataJSON.put("Previous Close", dataPoints[10]);
			dataJSON.put("52 Week Range", dataPoints[11]);
			dataJSON.put("52 Week High", dataPoints[12]);
			dataJSON.put("52 Week Low", dataPoints[13]);
			dataJSON.put("Dividend/Share", dataPoints[14]);
			dataJSON.put("Ex-Dividend Date", dataPoints[15]);
			dataJSON.put("Dividend Pay Date", dataPoints[16]);
			dataJSON.put("Dividend Yield", dataPoints[17]);
			dataString = dataJSON.toString() + "\n";
			/*****************************************************/
		}
		catch(SocketTimeoutException ste)
		{
			return null;
		}
		catch (MalformedURLException mur)
		{
			System.out.println("Something has gone wrong with data url! Code: " + code);
			mur.printStackTrace();
			return null;
		}
		catch (IOException ie)
		{
			System.out.println("Exception thrown when reading remote file! Code: " + code);
			ie.printStackTrace();
			return null;
		}
		return dataString;
	}
}

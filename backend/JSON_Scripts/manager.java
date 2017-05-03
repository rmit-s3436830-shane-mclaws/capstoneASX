package com.amazonaws.samples;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class manager
{
	protected static ArrayList<String> stockCodes = new ArrayList<String>();
	protected static String AccessKey = creds.accessKey;
	protected static String SecretKey = creds.secretAccessKey;
	protected static final String bucket = "asx-json-host";
	
	
	public static void main(String args[])
	{
		//Define Variables
		BufferedReader fileBuffer = null;
		String csvFile = "/home/ec2-user/ASX_JSON/companies.csv";
		String data;
		final int compPerThread = 500;
		/*****************************************************/
		try
		{
			//Open csv file containing company codes
			fileBuffer = new BufferedReader(new FileReader(csvFile));
		}
		catch (FileNotFoundException fnfe)
		{
			System.out.println("Error, CSV file not found!");
			fnfe.printStackTrace();
			return;
		}
		try
		{
			//Read data from file
			data = fileBuffer.readLine();
		}
		catch (IOException ie)
		{
			System.out.println("Exception thrown when reading CSV file!");
			ie.printStackTrace();
			return;
		}
		finally
		{
			try
			{
				fileBuffer.close();
			}
			catch (IOException ie)
			{
				System.out.println("Exception when closing file buffer!");
				ie.printStackTrace();
				return;
			}
		}
		//Create array of codes to download
		String codes[] = data.split(",");
		/*****************************************************/
		
		//Calculate number of threads required for download, each thread will handle 500 codes
		int numCompanies= codes.length;
		int numThreads = numCompanies / compPerThread;
		if(numCompanies % compPerThread != 0)
		{
			numThreads++;
		}
		Thread[] threads = new Thread[numThreads];
		/*****************************************************/
		
		//Starts each thread giving it a list of codes to download
		for(int i=0; i<numThreads; i++)
		{
			int firstCode = i*compPerThread;
			int lastCode = (i + 1) * compPerThread;
			if(lastCode  > numCompanies)
			{
				lastCode = numCompanies;
			}
			String[] subset = Arrays.copyOfRange(codes, firstCode, lastCode);
			threads[i] = new Thread(new downloader(subset));
			threads[i].start();
		}
		/*****************************************************/
		
		//Join the threads
		try
		{
			for(int i=0; i<numThreads; i++)
			{
				threads[i].join();
			}
		} 
		catch (InterruptedException ie)
		{
			System.out.println("Exception thrown when joining threads");
			ie.printStackTrace();
		}
		/*****************************************************/
		
		//Apply bubble sort to the ArrayList of stock codes after threads have been joined
		bubbleSortASX();
		/*****************************************************/
		
		//Generate csv string of asx codes from sorted ArrayList
		String csvCodes = "";
		for(String code:stockCodes)
		{
			csvCodes += code + ",";
		}
		csvCodes = csvCodes.substring(0, csvCodes.length()-1); //Remove last ',' from String
		/*****************************************************/
		
		//Save local copy of csv ASX Codes
		BufferedWriter fileWrite = null;
		try
		{
			fileWrite = new BufferedWriter(new FileWriter(csvFile));
		}
		catch(IOException fnfe)
		{
			System.out.println("Error, CSV file not found!");
			fnfe.printStackTrace();
		}
		try
		{
			fileWrite.write(csvCodes);
		}
		catch (IOException e)
		{
			System.out.println("Error writing to csv file!");
			e.printStackTrace();
		}
		finally
		{
			try
			{
				fileWrite.close();
			}
			catch(IOException e)
			{
				System.out.println("Error closing file stream!");
				e.printStackTrace();
			}
		}
		/*****************************************************/
		
		//Save csv String to S3 object
		String S3CsvFile = "companies.csv";
		AWSCredentials credentials = new BasicAWSCredentials(AccessKey,SecretKey);
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(Regions.AP_SOUTHEAST_2).build();
		try
		{
			byte[] contentAsBytes;
			contentAsBytes = csvCodes.getBytes("UTF-8");
			ByteArrayInputStream contentAsStream = new ByteArrayInputStream(contentAsBytes);
			ObjectMetadata md = new ObjectMetadata();
			md.setContentLength(contentAsBytes.length);
			s3Client.putObject(new PutObjectRequest(bucket, S3CsvFile, contentAsStream, md));
		}
		catch (UnsupportedEncodingException e)
		{
			System.out.println("Exception conversting csv codes to byte array");
			e.printStackTrace();
		}
		/*****************************************************/
	}
	
	private static void bubbleSortASX()
	{
		String stock, nextStock;
		for(int i=1; i<stockCodes.size(); i++)
		{
			for(int j=0; j<stockCodes.size()-i; j++)
			{
				stock = stockCodes.get(j);
				nextStock = stockCodes.get(j+1);
				if(stock.compareTo(nextStock) > 0) //yields true if alphabetically nextStock should be before stock
				{
					//swap positions of stock and nextStock within arraylist
					stockCodes.set(j, nextStock);
					stockCodes.set(j+1, stock);
				}
			}
		}
	}
}

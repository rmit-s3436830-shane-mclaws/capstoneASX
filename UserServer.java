package com.amazonaws.samples;

/************************************************************************
* This script will manage user accounts for the ASX trading game.      	*
* This server will be called when new users need to be added, or when 	*
* existing information needs to be retrieved.							*
************************************************************************/
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.InflaterInputStream;
import java.util.zip.DeflaterOutputStream;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class UserServer
{
	public static void main(String args[]) throws SocketException
	{
		Enumeration<NetworkInterface> iNets = NetworkInterface.getNetworkInterfaces();
        for(NetworkInterface iNet : Collections.list(iNets))
        {
        	System.out.printf("Display Name: %s\n",iNet.getDisplayName());
			Enumeration<InetAddress> addrs = iNet.getInetAddresses();
			for(InetAddress addr : Collections.list(addrs))
			{
				System.out.printf("\tLocal IP Address: %s\n",addr.getHostAddress());
			}
			
        }
		
		ServerSocket serverSock = null;
		Socket userConnection = null;
		BufferedReader connectionRead = null;
		DeflaterOutputStream deflStream = null;
		String line = null;
		byte[] bytes = null;
		try
		{
			serverSock = new ServerSocket(28543);
			serverSock.setReuseAddress(true);
			while(true)
			{
				userConnection = serverSock.accept();
				connectionRead = new BufferedReader(new InputStreamReader(new InflaterInputStream(userConnection.getInputStream())));
				line = connectionRead.readLine();
				if(line != null)
				{
					deflStream = new DeflaterOutputStream(userConnection.getOutputStream(), true);
					System.out.println(line);
					if(line.equals("login"))
					{
						String userID = connectionRead.readLine();
						String passwdHash = connectionRead.readLine();
						String details = null;
						if((details = login(userID,passwdHash)) != null)
						{
							bytes = details.getBytes("UTF-8");
						}
						else
						{
							bytes = "401".getBytes("UTF-8");
						}
					}
					else if(line.equals("register"))
					{
						String newPasswdHash = connectionRead.readLine();
						String newFName = connectionRead.readLine();
						String newSName = connectionRead.readLine();
						String newUEmail = connectionRead.readLine();
						if(register(newPasswdHash, newFName, newSName, newUEmail))
						{
							bytes = "200".getBytes("UTF-8");
						}
						else
						{
							bytes = "500".getBytes("UTF-8");
						}
					}
					else
					{
						bytes = "400: BAD REQUEST!".getBytes("UTF-8");
					}
					deflStream.write(bytes);
					deflStream.finish();
					deflStream.flush();
					userConnection.close();
				}
			}
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
	
	private static String login(String emailHash, String passwdHash)
	{
		//Search list of users for file called userID.rec
		//Check if hash inside userID.rec == passwdHash
		//If match, find file called userID.json and return contents
		//else return null;
		
		//Define known variables
		String bucket = "asx-user-store";
		String userCreds = "creds/"+emailHash+".rec";
		//Connect to S3
		AWSCredentials credentials = new BasicAWSCredentials(ASX ACCESS KEYS);
		AmazonS3 s3Client = new AmazonS3Client(credentials);
		//Grab user record into Buffered Reader Stream
		if(!s3Client.doesObjectExist(bucket, userCreds))
		{
			return null;
		}
		S3Object object = s3Client.getObject(new GetObjectRequest(bucket, userCreds));
		InputStream objectData = object.getObjectContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(objectData));
		try
		{
			String hash = reader.readLine();
			String userID = reader.readLine();
			reader.close();
			objectData.close();
			if(hash.equals(passwdHash))
			{
				String userData = "data/"+userID+".obj";
				if(!s3Client.doesObjectExist(bucket, userCreds))
				{
					return null;
				}
				object = s3Client.getObject(new GetObjectRequest(bucket, userData));
				objectData = object.getObjectContent();
				reader = new BufferedReader(new InputStreamReader(objectData));
				String data = reader.readLine();
				reader.close();
				objectData.close();
				return data;
			}
			else
			{
				return null;
			}
		}
		catch (IOException e)
		{
			System.out.println("Exception when reading S3 file: " + e);
		}
		finally
		{
			try
			{
				reader.close();
				objectData.close();
			}
			catch (IOException e)
			{
				System.out.println("Exception when closing streams: " + e);
			}
		}
		return null;
	}
	
	private static boolean register(String passwdHash, String fname, String sName, String uEmail)
	{
		//Creates a new instance of userID.json and userID.rec for future use
		String emailHash = Integer.toString(uEmail.hashCode());
		String bucket = "asx-user-store";
		String userCreds = "creds/"+emailHash+".rec";
		
		AWSCredentials credentials = new BasicAWSCredentials(ASX ACCESS KEYS);
		AmazonS3 s3Client = new AmazonS3Client(credentials);
		
		if(!s3Client.doesObjectExist(bucket, userCreds))
		{
			try
			{
				//Get list of existing .json files
				//create int uID = highest number.obj +1
				int uID = 0;
				int comp = 0;
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
								String key = summary.getKey();
								String[] keyArray = key.split("/");
								comp = Integer.parseInt(keyArray[1].substring(0, keyArray[1].length()-4));
								if(comp > uID)
								{
									uID = comp;
								}
							}
							objectList = s3Client.listNextBatchOfObjects(objectList);
						}while (objectList.isTruncated());
					}
					else
					{
						for(S3ObjectSummary summary : summaries)
						{
							String key = summary.getKey();
							String[] keyArray = key.split("/");
							comp = Integer.parseInt(keyArray[1].substring(0, keyArray[1].length()-4));
							if(comp > uID)
							{
								uID = comp;
							}
						}
					}
				}
				uID++;
				String userID = Integer.toString(uID);
				
				//Create user credentials file
				String recordData = passwdHash + "\n" + userID;
				byte[] contentAsBytes = recordData.getBytes("UTF-8");
				ByteArrayInputStream contentAsStream = new ByteArrayInputStream(contentAsBytes);
				ObjectMetadata md = new ObjectMetadata();
				md.setContentLength(contentAsBytes.length);
				s3Client.putObject(new PutObjectRequest(bucket, userCreds, contentAsStream, md));
				contentAsStream.close();
				
				//Create user data file
				String userData = "data/"+userID+".obj";
				String dataString = "{"+"'Name':'"+fname+"','Surname':'"+sName+"','Email':'"+uEmail+"','Balance':'1000000','Shares':[],'Rights':'trader'}";
				contentAsBytes = dataString.getBytes("UTF-8");
				contentAsStream = new ByteArrayInputStream(contentAsBytes);
				md = new ObjectMetadata();
				md.setContentLength(contentAsBytes.length);
				s3Client.putObject(new PutObjectRequest(bucket, userData, contentAsStream, md));
				contentAsStream.close();
				
				return true;
			}
			catch (AmazonServiceException ase)
			{
	            System.out.println("Caught an AmazonServiceException");
	            System.out.println("This means your request made it to Amazon S3, but was rejected with an error response.");
	            System.out.println("Error Message:    " + ase.getMessage());
	            System.out.println("HTTP Status Code: " + ase.getStatusCode());
	            System.out.println("AWS Error Code:   " + ase.getErrorCode());
	            System.out.println("Error Type:       " + ase.getErrorType());
	            System.out.println("Request ID:       " + ase.getRequestId());
			}
			catch (AmazonClientException ace)
			{
	            System.out.println("Caught an AmazonClientException");
	            System.out.println("This means the client encountered an internal error while trying to communicate with S3.");
	            System.out.println("Error Message: " + ace.getMessage());
			} catch (UnsupportedEncodingException e)
			{
				System.out.println("Caught an UnsupportedEncodingException");
			} catch (IOException e)
			{
				System.out.println("Exception whe trying to close stream");
			}
			return false;
		}
		return false;
	}
}

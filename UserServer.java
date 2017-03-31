package com.amazonaws.samples;
//Prototype implements acknowledgments on messages and implements a user save function/call

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
				System.out.println("===New incoming connection===");
				connectionRead = new BufferedReader(new InputStreamReader(new InflaterInputStream(userConnection.getInputStream())));
				line = connectionRead.readLine();
				if(line != null)
				{
					deflStream = new DeflaterOutputStream(userConnection.getOutputStream(), true);
					System.out.println("Requesting: " + line);
					if(line.equals("login"))
					{
						String userEmail = connectionRead.readLine();
						String passwdHash = connectionRead.readLine();
						String details = null;
						if((details = login(userEmail,passwdHash)) != null)
						{
							System.out.println("Returning: Valid Login Data");
							bytes = details.getBytes("UTF-8");
						}
						else
						{
							System.out.println("Returning: 401");
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
							System.out.println("Returning: 200");
							bytes = "200".getBytes("UTF-8");
						}
						else
						{
							System.out.println("Returning: 500");
							bytes = "500".getBytes("UTF-8");
						}
					}
					else if(line.equals("save"))
					{
						String userEmail = connectionRead.readLine();
						String userJson = connectionRead.readLine();
						if(save(userEmail, userJson))
						{
							System.out.println("Returning: 200");
							bytes = "200".getBytes("UTF-8");
						}
						else
						{
							System.out.println("Returning: 500");
							bytes = "500".getBytes("UTF-8");
						}
					}
					else if(line.equals("leaders"))
					{
						int topPos = Integer.parseInt(connectionRead.readLine());
						int count = Integer.parseInt(connectionRead.readLine());
						String leaders = "";
						if((leaders = leaderboard(topPos, count)) != null)
						{
							System.out.println("Returning: Leaderboard Data");
							bytes = leaders.getBytes("UTF-8");
						}
						else
						{
							System.out.println("Returning: 500");
							bytes = "500".getBytes("UTF-8");
						}
					}
					else
					{
						System.out.println("Returning: 400: BAD REQUEST!");
						bytes = "400: BAD REQUEST!".getBytes("UTF-8");
					}
					deflStream.write(bytes);
					deflStream.finish();
					deflStream.flush();
					userConnection.close();
					System.out.println("===End connection===\n");
				}
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
		catch (NumberFormatException ex)
		{
			System.out.println("Exception when converting String to Int: " + ex);
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
		//Search list of users for file called emailHash.rec
		//Check if hash inside emailHash.rec == passwdHash
		//If match, grab unique ID number from file
		//find file called uniqueIDNo.json and return contents
		//else return null
		
		//Define known variables
		String bucket = "asx-user-store";
		String userCreds = "creds/"+emailHash+".rec";
		//Connect to S3
		AWSCredentials credentials = new BasicAWSCredentials([REDACTED]);
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
		//Adds user to leader board with score 0
		String emailHash = Integer.toString(uEmail.hashCode());
		String bucket = "asx-user-store";
		String userCreds = "creds/"+emailHash+".rec";
		
		AWSCredentials credentials = new BasicAWSCredentials([REDACTED]);
		AmazonS3 s3Client = new AmazonS3Client(credentials);
		
		if(!s3Client.doesObjectExist(bucket, userCreds))
		{
			try
			{
				//Get list of existing .json files
				//create int uID = highest number.obj +1
				//all user IDs will be  digits minimum
				int uID = 9999;
				int comp = 9999;
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
				String dataString = "{"+"'Name':'"+fname+"','Surname':'"+sName+"','Email':'"+uEmail+"','Balance':'1000000.00','Shares':'','Score':'0.00','Rights':'trader'}";
				contentAsBytes = dataString.getBytes("UTF-8");
				contentAsStream = new ByteArrayInputStream(contentAsBytes);
				md = new ObjectMetadata();
				md.setContentLength(contentAsBytes.length);
				s3Client.putObject(new PutObjectRequest(bucket, userData, contentAsStream, md));
				contentAsStream.close();
				
				//Add user to leader board
				String leaderboard = "leaderboard.csv";
				String userEntry = userID + ":0\n";
				//Read Current leaderboard into \n seperated String
				S3Object object = s3Client.getObject(new GetObjectRequest(bucket, leaderboard));
				InputStream objectData = object.getObjectContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(objectData));
				String line = "";
				String fileContents = "";
				while((line = reader.readLine()) != null)
				{
					fileContents = fileContents + line + "\n";
				}
				reader.close();
				objectData.close();
				//Append new user entry to end of file and reupload
				fileContents = fileContents + userEntry;
				contentAsBytes = fileContents.getBytes("UTF-8");
				contentAsStream = new ByteArrayInputStream(contentAsBytes);
				md = new ObjectMetadata();
				md.setContentLength(contentAsBytes.length);
				s3Client.putObject(new PutObjectRequest(bucket, leaderboard, contentAsStream, md));
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
			}
			catch (UnsupportedEncodingException e)
			{
				System.out.println("Caught an UnsupportedEncodingException");
			}
			catch (IOException e)
			{
				System.out.println("Exception whe trying to close stream");
			}
			catch (NumberFormatException ex)
			{
				System.out.println("Exception when converting String to Int: " + ex);
			}
			return false;
		}
		return false;
	}
	
	private static boolean save(String emailHash, String newJSON)
	{
		//Search list of users for file called emailHash.rec
		//Grab unique ID number from record
		//Overwrite old ID.json with newJSON
		//Update leader board
		
		//Define known variables
		String bucket = "asx-user-store";
		String userCreds = "creds/"+emailHash+".rec";
		//Connect to S3
		AWSCredentials credentials = new BasicAWSCredentials([REDACTED]);
		AmazonS3 s3Client = new AmazonS3Client(credentials);
		//Grab user record into Buffered Reader Stream
		S3Object object = s3Client.getObject(new GetObjectRequest(bucket, userCreds));
		InputStream objectData = object.getObjectContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(objectData));
		try
		{
			reader.readLine();
			String userID = reader.readLine(); //Get userID number
			reader.close();
			objectData.close();
			String userData = "data/"+userID+".obj";
			
			//Overwrite old file with newJSON
			byte[] contentAsBytes = newJSON.getBytes("UTF-8");
			ByteArrayInputStream contentAsStream = new ByteArrayInputStream(contentAsBytes);
			ObjectMetadata md = new ObjectMetadata();
			md.setContentLength(contentAsBytes.length);
			s3Client.putObject(new PutObjectRequest(bucket, userData, contentAsStream, md));
			contentAsStream.close();
			
			//Update leaderboard
			String leaderboard = "leaderboard.csv";
			String[] data = newJSON.split(",");
			String scorePos = data[data.length - 2];
			String sScore = scorePos.split(":")[1].replaceAll("'", "");
			float iScore = Float.valueOf(sScore);
			String userEntry = userID + ":" + sScore + "\n";
			
			object = s3Client.getObject(new GetObjectRequest(bucket, leaderboard));
			objectData = object.getObjectContent();
			reader = new BufferedReader(new InputStreamReader(objectData));
			String line = "";
			String fileContents = "";
			boolean written = false;
			while(true)
			{
				line = reader.readLine();
				if(line != null)
				{
					String[] compData = line.split(":");
					String compUserID = compData[0];
					String compScoreString = compData[1];
					float compScore = Float.valueOf(compScoreString);
					if(!compUserID.equals(userID)) //If the current line does not belong to the updating user
					{
						System.out.println("Comparing two different users");
						if(written)
						{
							System.out.println("Copying existing line");
							fileContents = fileContents + line + "\n";
						}
						else if(iScore > compScore) //If users score is more than the current line being read
						{
							System.out.println("Placing user in new spot in leaderbaord");
							written = true;
							fileContents = fileContents + userEntry + line + "\n";
						}
						else
						{
							System.out.println("Copying existing line");
							fileContents = fileContents + line + "\n";
						}
					}
				}
				else if(!written) //reached end of file and player score is the lowest
				{
					System.out.println("Placing user in last spot");
					fileContents = fileContents + userEntry;
					break;
				}
				else
				{
					break;
				}
				
			}
			reader.close();
			objectData.close();
			//Write updated leaderboard to S3
			contentAsBytes = fileContents.getBytes("UTF-8");
			contentAsStream = new ByteArrayInputStream(contentAsBytes);
			md = new ObjectMetadata();
			md.setContentLength(contentAsBytes.length);
			s3Client.putObject(new PutObjectRequest(bucket, leaderboard, contentAsStream, md));
			contentAsStream.close();
			
			return true;
		}
		catch (IOException e)
		{
			System.out.println("Exception when overwriting S3 file: " + e);
		}
		catch (NumberFormatException ex)
		{
			System.out.println("Exception when converting String to Int: " + ex);
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
		return false;
	}
	
	private static String leaderboard(int topN, int count)
	{
		//Take the first topN entries from leaderboards.csv
		//If bottomN == 0, return entire leaderboard
		//Create a string to send back formatted as {'Name':'Name','sName':'sName','Score':'Score'},{},{},{} etc.
		//Return said string
		
		//Read leaderboard file line by line
		//Connect to S3
		AWSCredentials credentials = new BasicAWSCredentials([REDACTED]);
		AmazonS3 s3Client = new AmazonS3Client(credentials);
		String bucket = "asx-user-store";
		String leaderboard = "leaderboard.csv";
		S3Object object = s3Client.getObject(new GetObjectRequest(bucket, leaderboard));
		InputStream objectData = object.getObjectContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(objectData));
		try
		{
			String leaders = "";
			String line = "";
			int pos = 0;
			while(count > 0 && (line = reader.readLine()) != null)
			{
				if(pos >= topN)
				{
					count--;
					//grab user ID from line
					//grab user name, surname, and score from file
					String userID = line.split(":")[0].replaceAll("'", "");
					String score = line.split(":")[1].replaceAll("'", "");
					String userDataFile = "data/"+userID+".obj";
					S3Object userObject = s3Client.getObject(new GetObjectRequest(bucket, userDataFile));
					InputStream userObjectData = userObject.getObjectContent();
					BufferedReader userReader = new BufferedReader(new InputStreamReader(userObjectData));
					String userData = userReader.readLine();
					userReader.close();
					userObjectData.close();
					//Grab name and sName for leaders String
					String name = userData.split(",")[0].replace("{","");
					String sName = userData.split(",")[1];
					leaders = leaders + "{" + name + "," + sName + ",'Score':'" + score + "'}";
					leaders = leaders + ",";
				}
				pos++;
			}
			return leaders;
		}
		catch (IOException e)
		{
			System.out.println("Exception when returning leaderboard: " + e);
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
}

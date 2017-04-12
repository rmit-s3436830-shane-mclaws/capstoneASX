package com.amazonaws.samples;

/************************************************************************
* This class handles the individual connections, and responds to their	*
* requests with the appropriate data, or where applicable, error code	*
************************************************************************/

import java.io.*;
import java.net.*;
import java.util.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.zip.InflaterInputStream;
import java.util.zip.DeflaterOutputStream;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class threadedConnection implements Runnable
{
	private Socket client;
	private AWSCredentials credentials;
	private static AmazonS3 s3Client;
	private static final String bucket = "asx-user-store";
	
	public threadedConnection(Socket cSocket)
	{
		this.client = cSocket;
		this.credentials = new BasicAWSCredentials(REDACTED,REDACTED);
		threadedConnection.s3Client  = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(Regions.AP_SOUTHEAST_2).build();
	}
	
	public void run()
	{
		BufferedReader connectionRead = null;
		DeflaterOutputStream deflStream = null;
		String line = null;
		byte[] bytes = null;
		try
		{
			System.out.println("[" + this.client.getRemoteSocketAddress() + "] New incoming connection");
			connectionRead = new BufferedReader(new InputStreamReader(new InflaterInputStream(this.client.getInputStream())));
			line = connectionRead.readLine();
			if(line != null)
			{
				deflStream = new DeflaterOutputStream(this.client.getOutputStream(), true);
				System.out.println("[" + this.client.getRemoteSocketAddress() + "] Requesting: " + line);
				if(line.equals("login"))
				{
					String userEmail = connectionRead.readLine();
					String passwdHash = connectionRead.readLine();
					if((line = login(userEmail,passwdHash)) != null)
					{
						System.out.println("[" + this.client.getRemoteSocketAddress() + "] Returning: Valid Login Data");
						bytes = line.getBytes("UTF-8");
					}
					else
					{
						System.out.println("[" + this.client.getRemoteSocketAddress() + "] Returning: 401");
						bytes = "401".getBytes("UTF-8");
					}
				}
				else if(line.equals("history"))
				{
					String emailHash = connectionRead.readLine();
					String type = connectionRead.readLine();
					if((line = getHistory(emailHash,type)) != null)
					{
						System.out.println("[" + this.client.getRemoteSocketAddress() + "] Returning: Valid History Data");
						bytes = line.getBytes("UTF-8");
					}
					else
					{
						System.out.println("[" + this.client.getRemoteSocketAddress() + "] Returning: 500");
						bytes = "500".getBytes("UTF-8");
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
						System.out.println("[" + this.client.getRemoteSocketAddress() + "] Returning: 200");
						bytes = "200".getBytes("UTF-8");
					}
					else
					{
						System.out.println("[" + this.client.getRemoteSocketAddress() + "] Returning: 500");
						bytes = "500".getBytes("UTF-8");
					}
				}
				else if(line.equals("save"))
				{
					String userEmail = connectionRead.readLine();
					String userJson = connectionRead.readLine();
					String userTransaction = connectionRead.readLine();
					if(save(userEmail, userJson, userTransaction))
					{
						System.out.println("[" + this.client.getRemoteSocketAddress() + "] Returning: 200");
						bytes = "200".getBytes("UTF-8");
					}
					else
					{
						System.out.println("[" + this.client.getRemoteSocketAddress() + "] Returning: 500");
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
						System.out.println("[" + this.client.getRemoteSocketAddress() + "] Returning: Leaderboard Data");
						bytes = leaders.getBytes("UTF-8");
					}
					else
					{
						System.out.println("[" + this.client.getRemoteSocketAddress() + "] Returning: 500");
						bytes = "500".getBytes("UTF-8");
					}
				}
				else
				{
					System.out.println("[" + this.client.getRemoteSocketAddress() + "] Returning: 400: BAD REQUEST!");
					bytes = "400: BAD REQUEST!".getBytes("UTF-8");
				}
				deflStream.write(bytes);
				deflStream.finish();
				deflStream.flush();
				deflStream.close();
				this.client.close();
				System.out.println("[" + this.client.getRemoteSocketAddress() + "] End connection");
				return;
			}
		}
		catch (SocketException se)
		{
			System.out.println("[" + this.client.getRemoteSocketAddress() + "] Exception while performing socket operation: " + se);
		}
		catch(IOException e)
		{
			System.out.println("[" + this.client.getRemoteSocketAddress() + "] Exception while opening server socket: " + e);
		}
		catch (NumberFormatException ex)
		{
			System.out.println("[" + this.client.getRemoteSocketAddress() + "] Exception when converting String to Int: " + ex);
		}
		finally
		{
			try
			{
				deflStream.close();
				this.client.close();
			}
			catch (IOException e)
			{
				System.out.println("[" + this.client.getRemoteSocketAddress() + "] Exception while closing sockets: " + e);
			}
		}
		return;
	}
	
	private static String login(String emailHash, String passwdHash)
	{	
		//Define known variables
		String userCreds = "creds/"+emailHash+".rec";
		
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
				String userData = "data/"+userID+"/data.json";
				if(!s3Client.doesObjectExist(bucket, userCreds))
				{
					return null;
				}
				String data;
				//Get User Data
				object = s3Client.getObject(new GetObjectRequest(bucket, userData));
				objectData = object.getObjectContent();
				reader = new BufferedReader(new InputStreamReader(objectData));
				data = reader.readLine();
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
	
	private static String getHistory(String emailHash, String type)
	{
		//Define known variables
		String userCreds = "creds/"+emailHash+".rec";
		String line = "";
		String data = "";
		String file = "";
		
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
			reader.readLine();
			String userID = reader.readLine();
			reader.close();
			objectData.close();
			if(type.equals("transaction"))
			{
				//Get user purchase history
				file = "data/"+userID+"/purchaseHistory.json";
			}
			else if(type.equals("value"))
			{
				//Get user value history
				file = "data/"+userID+"/valueHistory.json";
			}
			object = s3Client.getObject(new GetObjectRequest(bucket, file));
			objectData = object.getObjectContent();
			reader = new BufferedReader(new InputStreamReader(objectData));
			while((line = reader.readLine()) != null)
			{
				data = line + "\n";
			}
			reader.close();
			objectData.close();
			return data;
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
				object.close();
			}
			catch (IOException e)
			{
				System.out.println("Exception when closing streams: " + e);
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private static boolean register(String passwdHash, String fname, String sName, String uEmail)
	{
		//Creates a new instance of userID.json and userID.rec for future use
		//Adds user to leader board with score 0
		String emailHash = Integer.toString(uEmail.hashCode());
		String userCreds = "creds/"+emailHash+".rec";
		
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
				String userData = "data/"+userID+"/data.json";
				String purchaseHistory = "data/"+userID+"/purchaseHistory.json";
				String valueHistory = "data/"+userID+"/valueHistory.json";
				//Create User JSON String
				JSONObject dataJSON = new JSONObject();
				dataJSON.put("Name", fname);
				dataJSON.put("Surname", sName);
				dataJSON.put("Email", uEmail);
				dataJSON.put("Balance", "1000000000.00");
				dataJSON.put("Shares","");
				dataJSON.put("Score","0.00");
				dataJSON.put("Rights","trader");
				String dataString = dataJSON.toString();
				//Create History Strings
				String pHisString = "";
				String vHisString = "";
				
				//Write Data file
				contentAsBytes = dataString.getBytes("UTF-8");
				contentAsStream = new ByteArrayInputStream(contentAsBytes);
				md = new ObjectMetadata();
				md.setContentLength(contentAsBytes.length);
				s3Client.putObject(new PutObjectRequest(bucket, userData, contentAsStream, md));
				contentAsStream.close();
				//Write purchaseHistory File
				contentAsBytes = pHisString.getBytes("UTF-8");
				contentAsStream = new ByteArrayInputStream(contentAsBytes);
				md = new ObjectMetadata();
				md.setContentLength(contentAsBytes.length);
				s3Client.putObject(new PutObjectRequest(bucket, purchaseHistory, contentAsStream, md));
				contentAsStream.close();
				//Write valueHistory File
				contentAsBytes = vHisString.getBytes("UTF-8");
				contentAsStream = new ByteArrayInputStream(contentAsBytes);
				md = new ObjectMetadata();
				md.setContentLength(contentAsBytes.length);
				s3Client.putObject(new PutObjectRequest(bucket, valueHistory, contentAsStream, md));
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
	
	private static boolean save(String emailHash, String newJSON, String transaction)
	{
		//Define known variables
		String userCreds = "creds/"+emailHash+".rec";
		String line = "";
		
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
			String userData = "data/"+userID+"/data.json";
			String dataString = newJSON + "\n";
			
			//Update Data file
			byte[] contentAsBytes = dataString.getBytes("UTF-8");
			ByteArrayInputStream contentAsStream = new ByteArrayInputStream(contentAsBytes);
			ObjectMetadata md = new ObjectMetadata();
			md.setContentLength(contentAsBytes.length);
			s3Client.putObject(new PutObjectRequest(bucket, userData, contentAsStream, md));
			
			//Read transaction history file append new data to the end of it
			if(transaction != null)
			{
				String transactionHistory = "data/"+userID+"/purchaseHistory.json";
				object = s3Client.getObject(new GetObjectRequest(bucket, transactionHistory));
				objectData = object.getObjectContent();
				reader = new BufferedReader(new InputStreamReader(objectData));
				dataString = "";
				while((line = reader.readLine()) != null)
				{
					dataString += line + "\n";
				}
				dataString += transaction + "\n";
				
				contentAsBytes = dataString.getBytes("UTF-8");
				contentAsStream = new ByteArrayInputStream(contentAsBytes);
				md = new ObjectMetadata();
				md.setContentLength(contentAsBytes.length);
				s3Client.putObject(new PutObjectRequest(bucket, transactionHistory, contentAsStream, md));
				contentAsStream.close();
			}
			
			//Update leaderboard
			String leaderboard = "leaderboard.csv";
			JSONParser parser = new JSONParser();
			JSONObject data = (JSONObject) parser.parse(newJSON);
			String sScore = data.get("Score").toString();
			float fScore = Float.valueOf(sScore);
			String userEntry = userID + ":" + sScore + "\n";
			
			object = s3Client.getObject(new GetObjectRequest(bucket, leaderboard));
			objectData = object.getObjectContent();
			reader = new BufferedReader(new InputStreamReader(objectData));
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
						if(written)
						{
							fileContents = fileContents + line + "\n";
						}
						else if(fScore > compScore) //If users score is more than the current line being read
						{
							written = true;
							fileContents = fileContents + userEntry + line + "\n";
						}
						else
						{
							fileContents = fileContents + line + "\n";
						}
					}
				}
				else if(!written) //reached end of file and player score is the lowest
				{
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
		catch (ParseException pe)
		{
			System.out.println("Exception when parsing JSON string: " + pe);
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
					leaders = leaders + ";";
				}
				pos++;
			}
			leaders = leaders.substring(0, leaders.length() - 1);
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

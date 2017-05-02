package com.amazonaws.samples;

/************************************************************************
* This class handles the individual connections, and responds to their	*
* requests with the appropriate data, or where applicable, error code	*
************************************************************************/

import java.io.*;
import java.net.*;
import java.util.*;
import org.json.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

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
	private static String date;
	private static String time;
	private static String  fileName;
	private AWSCredentials credentials;
	private static AmazonS3 s3Client;
	private static final String bucket = "asx-user-store";
	private static String AccessKey = "REDACTED";
	private static String SecretKey = "REDACTED";
	private static String fileData;
	
	public threadedConnection(Socket cSocket)
	{
		this.client = cSocket;
		this.credentials = new BasicAWSCredentials(threadedConnection.AccessKey,threadedConnection.SecretKey);
		threadedConnection.s3Client  = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(Regions.AP_SOUTHEAST_2).build();
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		DateFormat tf = new SimpleDateFormat("HHmmss");
		df.setTimeZone(TimeZone.getTimeZone("Australia/Victoria"));
		tf.setTimeZone(TimeZone.getTimeZone("Australia/Victoria"));
		Date dateobj = new Date();
		threadedConnection.date = df.format(dateobj);
		threadedConnection.time = tf.format(dateobj);
		threadedConnection.fileName = "serverLogs/" + date + "-" + time + ".log";
	}
	
	public void run()
	{
		BufferedReader connectionRead = null;
		DeflaterOutputStream deflStream = null;
		BufferedWriter fileWrite = null;
		fileData = "";
		String line = null;
		byte[] bytes = null;
		try
		{
			fileWrite = new BufferedWriter(new FileWriter(threadedConnection.fileName));
			//System.out.println("[" + this.client.getRemoteSocketAddress() + "] New incoming connection");
			fileData += "New incoming connection [" + this.client.getRemoteSocketAddress() + "] \n";
			connectionRead = new BufferedReader(new InputStreamReader(new InflaterInputStream(this.client.getInputStream())));
			line = connectionRead.readLine();
			if(line != null)
			{
				deflStream = new DeflaterOutputStream(this.client.getOutputStream(), true);
				//System.out.println("[" + this.client.getRemoteSocketAddress() + "] Requesting: " + line);
				if(line.equals("login"))
				{
					String userEmail = connectionRead.readLine();
					String passwdHash = connectionRead.readLine();
					fileData += "Requesting: " + line + "; Email Hash: " + userEmail + "; Password Hash: " + passwdHash + "\n";
					if((line = login(userEmail,passwdHash)) != null)
					{
						//System.out.println("[" + this.client.getRemoteSocketAddress() + "] Returning: Valid Login Data");
						fileData += "Returning: Valid Login Data\n";
						fileData += line + "\n";
						bytes = line.getBytes("UTF-8");
					}
					else
					{
						//System.out.println("[" + this.client.getRemoteSocketAddress() + "] Returning: 401");
						fileData += "Returning: 401\n";
						bytes = "401".getBytes("UTF-8");
					}
				}
				else if(line.equals("history"))
				{
					String emailHash = connectionRead.readLine();
					String type = connectionRead.readLine();
					fileData += "Requesting: " + line + "; Email Hash: " + emailHash + "; Type: " + type + "\n";
					if((line = getHistory(emailHash,type)) != null)
					{
						//System.out.println("[" + this.client.getRemoteSocketAddress() + "] Returning: Valid History Data");
						fileData += "Returning: Valid History Data\n";
						fileData += line + "\n";
						bytes = line.getBytes("UTF-8");
					}
					else
					{
						//System.out.println("[" + this.client.getRemoteSocketAddress() + "] Returning: 500");
						fileData += "Returning: 500\n";
						bytes = "500".getBytes("UTF-8");
					}
				}
				else if(line.equals("register"))
				{
					String newPasswdHash = connectionRead.readLine();
					String newFName = connectionRead.readLine();
					String newSName = connectionRead.readLine();
					String newUEmail = connectionRead.readLine();
					fileData += "Requesting: " + line + "; Password Hash: " + newPasswdHash + "; First Name: " + newFName + "; Surname: " + newSName + "; Email: " + newUEmail +"\n";
					if(register(newPasswdHash, newFName, newSName, newUEmail))
					{
						//System.out.println("[" + this.client.getRemoteSocketAddress() + "] Returning: 200");
						fileData += "Returning: 200\n";
						bytes = "200".getBytes("UTF-8");
					}
					else
					{
						//System.out.println("[" + this.client.getRemoteSocketAddress() + "] Returning: 500");
						fileData += "Returning: 500\n";
						bytes = "500".getBytes("UTF-8");
					}
				}
				else if(line.equals("save"))
				{
					String userEmail = connectionRead.readLine();
					String userJson = connectionRead.readLine();
					String userTransaction = connectionRead.readLine();
					fileData += "Requesting: " + line + "; Email Hash: " + userEmail + "\n JSON String: " + userJson + "\n Transaction String: " + userTransaction + "\n";
					if(save(userEmail, userJson, userTransaction))
					{
						//System.out.println("[" + this.client.getRemoteSocketAddress() + "] Returning: 200");
						fileData += "Returning: 200\n";
						bytes = "200".getBytes("UTF-8");
					}
					else
					{
						//System.out.println("[" + this.client.getRemoteSocketAddress() + "] Returning: 500");
						fileData += "Returning: 500\n";
						bytes = "500".getBytes("UTF-8");
					}
				}
				else if(line.equals("leaders"))
				{
					int topPos = Integer.parseInt(connectionRead.readLine());
					int count = Integer.parseInt(connectionRead.readLine());
					fileData += "Requesting: " + line + "; Top Position: " + topPos + "; Num: " + count +  "\n";
					String leaders = "";
					if((leaders = leaderboard(topPos, count)) != null)
					{
						//System.out.println("[" + this.client.getRemoteSocketAddress() + "] Returning: Leaderboard Data");
						fileData += "Returning: Leaderboard Data\n";
						fileData += leaders + "\n";
						bytes = leaders.getBytes("UTF-8");
					}
					else
					{
						//System.out.println("[" + this.client.getRemoteSocketAddress() + "] Returning: 500");
						fileData += "Returning: 500\n";
						bytes = "500".getBytes("UTF-8");
					}
				}
				else if(line.equals("getUser"))
				{
					String emailHash = connectionRead.readLine();
					fileData += "Requesting: " + line + "; Email Hash: " + emailHash + "\n";
					String response = "";
					if((response = getUsers(emailHash)) != null)
					{
						//System.out.println("[" + this.client.getRemoteSocketAddress() + "] Returning: User Data");
						fileData += "Returning: User Data\n";
						fileData += response + "\n";
						bytes = response.getBytes("UTF-8");
					}
					else
					{
						//System.out.println("[" + this.client.getRemoteSocketAddress() + "] Returning: 500");
						fileData += "Returning: 500\n";
						bytes = "500".getBytes("UTF-8");
					}
				}
				else if(line.equals("stockHistory"))
				{
					String stockCode = connectionRead.readLine();
					int startDate = Integer.parseInt(connectionRead.readLine());
					int endDate = Integer.parseInt(connectionRead.readLine());
					fileData += "Requesting: " + line + "; ASX Code: " + stockCode + "; Start Date: " + startDate + "; End Date: " + endDate + "\n";
					String response = "";
					if((response = stockHistory(stockCode, startDate, endDate)) != null)
					{
						response = "200\n" + response;
						fileData += "Returning: Stock History Data\n";
						fileData += response + "\n";
						bytes = response.getBytes("UTF-8");
					}
					else
					{
						fileData += "Returning: 500\n";
						bytes = "500".getBytes("UTF-8");
					}
				}
				else if(line.equals("setBuy"))
				{
					try
					{
						double flat = Double.parseDouble(connectionRead.readLine());
						double percentage = Double.parseDouble(connectionRead.readLine());
						fileData += "Requesting: " + line + "; Flat: " + flat + "; Percentage: " + percentage + "\n";
						UserServer.flatBuyFee = flat;
						UserServer.perBuyFee = percentage;
						fileData += "Returning: 200\n";
						bytes = "200".getBytes("UTF-8");
					}
					catch (NumberFormatException nfe)
					{
						fileData += "Returning: 400\n";
						bytes = "400".getBytes("UTF-8");
					}
				}
				else if(line.equals("setSell"))
				{
					try
					{
						double flat = Double.parseDouble(connectionRead.readLine());
						double percentage = Double.parseDouble(connectionRead.readLine());
						fileData += "Requesting: " + line + "; Flat: " + flat + "; Percentage: " + percentage + "\n";
						UserServer.flatSellFee = flat;
						UserServer.perSellFee = percentage;
						fileData += "Returning: 200\n";
						bytes = "200".getBytes("UTF-8");
					}
					catch (NumberFormatException nfe)
					{
						fileData += "Returning: 400\n";
						bytes = "400".getBytes("UTF-8");
					}
				}
				else if(line.equals("getBuy"))
				{
					try
					{
						fileData += "Requesting: " + line + "\n";
						String response = "200\n" + UserServer.flatBuyFee + "\n" + UserServer.perBuyFee + "\n";
						fileData += "Returning: " + response;
						bytes = response.getBytes("UTF-8");
					}
					catch (NumberFormatException nfe)
					{
						fileData += "Returning: 500\n";
						bytes = "500".getBytes("UTF-8");
					}
				}
				else if(line.equals("getSell"))
				{
					try
					{
						fileData += "Requesting: " + line + "\n";
						String response = "200\n" + UserServer.flatSellFee + "\n" + UserServer.perSellFee + "\n";
						fileData += "Returning: " + response;
						bytes = response.getBytes("UTF-8");
					}
					catch (NumberFormatException nfe)
					{
						fileData += "Returning: 500\n";
						bytes = "500".getBytes("UTF-8");
					}
				}
				else if(line.equals("sendMessage"))
				{
					String sender = connectionRead.readLine();
					String recipient = connectionRead.readLine();
					String type = connectionRead.readLine();
					String contents = "";
					while((line = connectionRead.readLine()) != null)
					{
						contents += line + "\n";
					}
					fileData += "Requesting: " + line + "; Sender: " + sender + "; Recipient: " + recipient + "; Type: " + type + "\nContents: " + contents + "\n";
					if(sendMessage(sender, recipient, type, contents))
					{
						fileData += "Returning: 200\n";
						bytes = "200".getBytes("UTF-8");
					}
					else
					{
						fileData += "Returning: 500\n";
						bytes = "500".getBytes("UTF-8");
					}
				}
				else
				{
					while((line = connectionRead.readLine()) != null)
					{
						line += line + "/n";
					}
					fileData  += "Requested: " + line + "\n";
					fileData  += "Returning: 400: BAD REQUEST!\n";
					bytes = "400: BAD REQUEST!".getBytes("UTF-8");
				}
				deflStream.write(bytes);
				deflStream.finish();
				deflStream.flush();
				deflStream.close();
				this.client.close();
				//System.out.println("[" + this.client.getRemoteSocketAddress() + "] End connection");
				fileData += "End connection [" + this.client.getRemoteSocketAddress() + "] \n";
				return;
			}
		}
		catch (SocketException se)
		{
			System.out.println("[" + this.client.getRemoteSocketAddress() + "] Exception while performing socket operation: " + se);
			fileData += "Exception while performing socket operation: " + se + "\n";
		}
		catch(IOException e)
		{
			System.out.println("[" + this.client.getRemoteSocketAddress() + "] Exception while opening server socket: " + e);
			fileData += "Exception while opening server socket: " + e + "\n";
		}
		catch (NumberFormatException ex)
		{
			System.out.println("[" + this.client.getRemoteSocketAddress() + "] Exception when converting String to Int: " + ex);
			fileData += "Exception when converting String to Int: " + ex + "\n";
		}
		finally
		{
			try
			{
				deflStream.close();
				this.client.close();
				fileWrite.write(fileData);
				fileWrite.close();
			}
			catch (IOException e)
			{
				System.out.println("[" + this.client.getRemoteSocketAddress() + "] Exception while closing sockets: " + e);
				fileData += "Exception while closing sockets: " + e + "\n";
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
				String transHistory = "data/"+userID+"/purchaseHistory.json";
				String valueHistory = "data/"+userID+"/valueHistory.json";
				if(!s3Client.doesObjectExist(bucket, userData))
				{
					return null;
				}
				
				//Get User Data
				String data;
				object = s3Client.getObject(new GetObjectRequest(bucket, userData));
				objectData = object.getObjectContent();
				reader = new BufferedReader(new InputStreamReader(objectData));
				data = reader.readLine() + "\n";
				reader.close();
				objectData.close();
				
				//Get User Transaction History
				data += "transaction\n";
				object = s3Client.getObject(new GetObjectRequest(bucket, transHistory));
				objectData = object.getObjectContent();
				reader = new BufferedReader(new InputStreamReader(objectData));
				String line = "";
				while((line = reader.readLine()) != null)
				{
					data += line + "\n";
				}
				reader.close();
				objectData.close();
				
				//Get User Value History
				data += "value\n";
				object = s3Client.getObject(new GetObjectRequest(bucket, valueHistory));
				objectData = object.getObjectContent();
				reader = new BufferedReader(new InputStreamReader(objectData));
				line = "";
				while((line = reader.readLine()) != null)
				{
					data += line + "\n";
				}
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
			fileData += "Login: Exception when reading S3 file: " + e + "\n";
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
				fileData += "Login: Exception when closing streams: " + e + "\n";
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
			fileData += "getHistory: Exception when reading S3 file: " + e + "\n";
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
				fileData += "getHistory: Exception when closing streams: " + e + "\n";
			}
		}
		return null;
	}
	
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
								comp = Integer.parseInt(keyArray[1].split(".")[0]);
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
							comp = Integer.parseInt(keyArray[1]);
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
				dataJSON.put("Balance", "1000000.00");
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
	            fileData += "register: Caught an AmazonServiceException: " + ase + "\n";
			}
			catch (AmazonClientException ace)
			{
	            System.out.println("Caught an AmazonClientException: " + ace);
	            fileData += "register: Caught an AmazonClientException: " + ace + "\n";
			}
			catch (UnsupportedEncodingException e)
			{
				System.out.println("Caught an UnsupportedEncodingException");
				fileData += "register: Caught an UnsupportedEncodingException: " + e + "\n";
			}
			catch (IOException e)
			{
				System.out.println("Exception whe trying to close stream");
				fileData += "register: Exception whe trying to close stream: " + e + "\n";
			}
			catch (NumberFormatException ex)
			{
				System.out.println("Exception when converting String to Int: " + ex);
				fileData += "register: Exception when converting String to Int: " + ex + "\n";
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
				dataString += transaction;
				
				contentAsBytes = dataString.getBytes("UTF-8");
				contentAsStream = new ByteArrayInputStream(contentAsBytes);
				md = new ObjectMetadata();
				md.setContentLength(contentAsBytes.length);
				s3Client.putObject(new PutObjectRequest(bucket, transactionHistory, contentAsStream, md));
				contentAsStream.close();
			}
			
			//Update leaderboard
			String leaderboard = "leaderboard.csv";
			JSONObject data = new JSONObject(newJSON);
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
			fileData += "save: Exception when overwriting S3 file: " + e + "\n";
		}
		catch (NumberFormatException ex)
		{
			System.out.println("Exception when converting String to Int: " + ex);
			fileData += "save: Exception when converting String to Int: " + ex + "\n";
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
				fileData += "save: Exception when closing streams: " + e + "\n";
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
					String userDataFile = "data/"+userID+"/data.json";
					S3Object userObject = s3Client.getObject(new GetObjectRequest(bucket, userDataFile));
					InputStream userObjectData = userObject.getObjectContent();
					BufferedReader userReader = new BufferedReader(new InputStreamReader(userObjectData));
					String userData = userReader.readLine();
					userReader.close();
					userObjectData.close();
					//Grab name and sName for leaders String
					JSONObject data = new JSONObject(userData);
					String name = data.get("Name").toString();
					String sName = data.get("Surname").toString();
					leaders = leaders + "{'Name':'" + name + "','Surname':'" + sName + "','Score':'" + score + "'}";
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
			fileData += "leaderboard: Exception when returning leaderboard: " + e + "\n";
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
				fileData += "leaderboard: Exception when closing streams: " + e + "\n";
			}
		}
		return null;
	}
	
	private static String getUsers(String emailHash)
	{
		//returns a list of all users in format of {fName,sName,email}\n{fName,sName,email}\n etc
		if(emailHash.equals("*"))
		{
			List<Integer> users = new ArrayList<Integer>();
			ObjectListing objectList = s3Client.listObjects(bucket, "data/");
			List<S3ObjectSummary> summaries = objectList.getObjectSummaries();
			summaries.remove(0);
			int currID = 0;
			//Get list of all userID's and put it into Integer List 'Users'
			if(summaries.size() != 0)
			{
				if(objectList.isTruncated())
				{
					do
					{
						for(S3ObjectSummary summary : summaries)
						{
							currID = Integer.parseInt(summary.getKey().split("/")[1]);
							if(users.indexOf(currID) == -1) //If element hasn't already been added to list add it to list
							{
								users.add(currID);
							}
						}
						objectList = s3Client.listNextBatchOfObjects(objectList);
					}while (objectList.isTruncated());
				}
				else
				{
					for(S3ObjectSummary summary : summaries)
					{
						currID = Integer.parseInt(summary.getKey().split("/")[1]);
						if(users.indexOf(currID) == -1) //If element hasn't already been added to list add it to list
						{
							users.add(currID);
						}
					}
				}
			}
			
			String userList = "";
			for(int iUser : users)
			{
				//Grab users first name, last name, email, create JSON object, convert to string, append to end of return
				try
				{
					//Grab user JSON data
					String userData = "data/" + iUser + "/data.json";
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
					
					//Generate new JSON Object
					JSONObject userDetails = new JSONObject();
					
					//Grab first name, surname, email from data
					userDetails.put("Name",dataJSON.get("Name"));
					userDetails.put("Surname",dataJSON.get("Surname"));
					userDetails.put("Email",dataJSON.get("Email"));
					
					//Convert to string and append to end of userList
					userList += userDetails.toString() + "\n";
				}
				catch (IOException ie)
				{
					System.out.println("Exception when reading " + iUser + "'s user data: " + ie);
					fileData += "getUsers: Exception when reading " + iUser + "'s user data: " + ie + "\n";
				}
			}
			return userList;
		}
		else //return full data + transaction history string for a single user
		{
			try
			{
				//Define known variables
				String userCreds = "creds/"+emailHash+".rec";
				
				//Get User ID Number from email
				if(!s3Client.doesObjectExist(bucket, userCreds))
				{
					return null;
				}
				S3Object object = s3Client.getObject(new GetObjectRequest(bucket, userCreds));
				InputStream objectData = object.getObjectContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(objectData));
				reader.readLine(); //Skip password hash
				String userID = reader.readLine();
				reader.close();
				objectData.close();
				
				String userData = "data/"+userID+"/data.json";
				String transHistory = "data/"+userID+"/purchaseHistory.json";
				if(!s3Client.doesObjectExist(bucket, userData))
				{
					return null;
				}
				
				//Get User Data
				String data;
				object = s3Client.getObject(new GetObjectRequest(bucket, userData));
				objectData = object.getObjectContent();
				reader = new BufferedReader(new InputStreamReader(objectData));
				data = reader.readLine() + "\n"; //Read user data String
				reader.close();
				objectData.close();
				
				//Get User Transaction History
				object = s3Client.getObject(new GetObjectRequest(bucket, transHistory));
				objectData = object.getObjectContent();
				reader = new BufferedReader(new InputStreamReader(objectData));
				String line = "";
				while((line = reader.readLine()) != null)
				{
					data += line + "\n"; //Append each line of transaction history into data String
				}
				reader.close();
				objectData.close();
				
				return data;
			}
			catch (IOException ie)
			{
				System.out.println("Exception when reading user data: " + ie);
				fileData += "getUsers: Exception when reading user data: " + ie + "\n";
			}
		}
		return null;
	}
	
	private String stockHistory(String asxCode, int startDate, int endDate)
	{
		String prefix = asxCode+"/";
		String dataReturn = "";
		String ASXJSON = "asx-json-host";
		ArrayList<String> files = new ArrayList<String>();
		ObjectListing objectList = s3Client.listObjects(ASXJSON, prefix);
		List<S3ObjectSummary> summaries = objectList.getObjectSummaries();
		if(summaries.size() != 0)
		{
			if(objectList.isTruncated())
			{
				do
				{
					for(S3ObjectSummary summary : summaries)
					{
						String file = summary.getKey().split("[/.]")[1];
						//String[] sDate = file.split(".");
						int fileDate = Integer.parseInt(file);	//key should read "asx/20170214.json", fileDate should result in 20170214
						if(fileDate >= startDate && fileDate <= endDate)
						{
							files.add(summary.getKey());
						}
					}
					objectList = s3Client.listNextBatchOfObjects(objectList);
				}while (objectList.isTruncated());
			}
			else
			{
				for(S3ObjectSummary summary : summaries)
				{
					String file = summary.getKey().split("[/.]")[1];
					//String[] sDate = file.split(".");
					int fileDate = Integer.parseInt(file);	//key should read "asx/20170214.json", fileDate should result in 20170214
					if(fileDate >= startDate && fileDate <= endDate)
					{
						files.add(summary.getKey());
					}
				}
			}
		}
		for(String key:files)
		{
			//Open file, add contents to return data
			S3Object object = s3Client.getObject(new GetObjectRequest(ASXJSON, key));
			InputStream objectData = object.getObjectContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(objectData));
			String date = key.split("[/.]")[1];
			String data = "";
			String prevData = "";
			try
			{
				while((data = reader.readLine()) != null)
				{
					prevData = data; //Ensures that prevData will be the last line of data when the loop terminates
				}
				reader.close();
				objectData.close();
				JSONObject tmp = new JSONObject(prevData);
				JSONObject jData = new JSONObject();
				jData.put("ASX Code",  tmp.get("ASX Code"));
				jData.put("Date", date);
				jData.put("Ask Price", tmp.get("Ask Price"));
				dataReturn += jData.toString() + "\n";
			}
			catch (IOException ie)
			{
				System.out.println("Exception when reading ASX data: " + ie);
				fileData += "stockHistory: Exception when reading ASX data: " + ie + "\n";
			}
		}
		return dataReturn;
	}
	
	private boolean sendMessage(String sender, String recipient, String type, String contents)
	{
		/**Convert senders emailHash to email address**/
		String senderCreds = "creds/"+sender+".rec";
		String ID = "";
		String line = "";
		//Grab sender record into Buffered Reader Stream
		S3Object object = s3Client.getObject(new GetObjectRequest(bucket, senderCreds));
		InputStream objectData = object.getObjectContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(objectData));
		try
		{
			reader.readLine();  //Skip password hash line
			ID = reader.readLine(); //Get recipients ID number
		}
		catch(IOException ie)
		{
			System.out.println("Exception when reading senders record file: " + ie);
			fileData += "sendMessage: Exception when reading senders record file: " + ie + "\n";
			return false;
		}
		//Open senders data file and grab email field
		String senderData = "data/"+ID+"/data.json";
		object = s3Client.getObject(new GetObjectRequest(bucket, senderData));
		objectData = object.getObjectContent();
		reader = new BufferedReader(new InputStreamReader(objectData));
		try
		{
			line = reader.readLine();
		}
		catch(IOException ie)
		{
			System.out.println("Exception when reading senders data file: " + ie);
			fileData += "sendMessage: Exception when reading senders data file: " + ie + "\n";
			return false;
		}
		JSONObject senderJSON = new JSONObject(line);
		String senderEmail = senderJSON.getString("Email");
		
		/**Create JSON object to be posted in users mailbox**/
		JSONObject newMail = new JSONObject();
		newMail.put("Sender", senderEmail);
		newMail.put("Type", type);
		newMail.put("Contents", contents);
		String mailEntry = newMail.toString();
		
		/**Find user to send mail to**/
		//Get recipients unique ID from creds file
		String recipientCreds = "creds/"+recipient+".rec";
		if(s3Client.doesObjectExist(bucket, recipientCreds))
		{
			object = s3Client.getObject(new GetObjectRequest(bucket, recipientCreds));
			objectData = object.getObjectContent();
			reader = new BufferedReader(new InputStreamReader(objectData));
			line = "";
			try
			{
				reader.readLine();  //Skip password hash line
				ID = reader.readLine(); //Get recipients ID number
			}
			catch(IOException ie)
			{
				System.out.println("Exception when reading recipients record file: " + ie);
				fileData += "sendMessage: Exception when reading recipients record file: " + ie + "\n";
				return false;
			}
			/**Post message to recipients mailbox folder**/
			//Generate message number
			ObjectListing objectList = s3Client.listObjects(bucket, "data/"+ID+"/mailbox/");
			List<S3ObjectSummary> summaries = objectList.getObjectSummaries();
			int mailID = 999; //minimum number for mailID is 1000
			int comp;
			if(summaries.size() != 0)
			{
				if(objectList.isTruncated())
				{
					do
					{
						for(S3ObjectSummary summary : summaries)
						{
							String key = summary.getKey();
							comp = Integer.parseInt(key.split("[/.]")[3]);
							if(comp > mailID)
							{
								mailID = comp;
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
						comp = Integer.parseInt(key.split("[/.]")[3]);
						if(comp > mailID)
						{
							mailID = comp;
						}
					}
				}
			}
			mailID++;
			String mailPath = "data/"+ID+"/mailbox/"+mailID+".JSON";
			//Post mail to User
			byte[] contentAsBytes = null;
			try 
			{
				contentAsBytes = mailEntry.getBytes("UTF-8");
			} 
			catch (UnsupportedEncodingException uee)
			{
				System.out.println("Exception when converting mail JSON to bytes: " + uee);
				fileData += "sendMessage: Exception when converting mail JSON to bytes: " + uee + "\n";
				return false;
			}
			ByteArrayInputStream contentAsStream = new ByteArrayInputStream(contentAsBytes);
			ObjectMetadata md = new ObjectMetadata();
			md.setContentLength(contentAsBytes.length);
			s3Client.putObject(new PutObjectRequest(bucket, mailPath, contentAsStream, md));
			try
			{
				contentAsStream.close();
			}
			catch(IOException ie)
			{
				System.out.println("Exception when closing stream to S3: " + ie);
				fileData += "sendMessage: Exception when closing stream to S3: " + ie + "\n";
				return false;
			}
		}
		else
		{
			System.out.println("Recipient does not exist!");
			fileData += "sendMessage: Recipient does not exist!";
			return false;
		}		
		return true;
	}
}

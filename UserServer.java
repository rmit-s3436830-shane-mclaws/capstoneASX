/************************************************************************
* This script will manage user accounts for the ASX trading game.      	*
* This server will be called when new users need to be added, or when 	*
* existing information needs to be retrieved.							*
************************************************************************/
import java.io.*;
import java.net.*;
import java.util.*;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.GetObjectRequest;
import static java.lang.System.out;

public class UserServer
{
	public static void main(String args[]) throws SocketException
	{
		Enumeration<NetworkInterface> iNets = NetworkInterface.getNetworkInterfaces();
        for(NetworkInterface iNet : Collections.list(iNets))
        {
        	out.printf("Dispaly Name: %s\n",iNet.getDisplayName());
			Enumeration<InetAddress> addrs = iNet.getInetAddresses();
			for(InetAddress addr : Collections.list(addrs))
			{
				out.printf("\tLocal IP Address: %s\n",addr.getHostAddress());
			}
			
        }
		
		ServerSocket serverSock = null;
		Socket userConnection = null;
		
		try
		{
			serverSock = new ServerSocket(38543);
			while(serverSock != null)
			{
				userConnection = serverSock.accept();
				out.printf("Client Socket Address: %s\n", userConnection.getRemoteSocketAddress());
				
				//Inputs
				BufferedReader connectionRead = new BufferedReader(new InputStreamReader(userConnection.getInputStream()));
				String line = null;
				//Outputs
				PrintWriter out = new PrintWriter(userConnection.getOutputStream(), true);
				
				while((line = connectionRead.readLine()) != null)
				{
					System.out.println(line + "\n");
					if(line.equals("login"))
					{
						String userID = connectionRead.readLine();
						String passwdHash = connectionRead.readLine();
						String details = null;
						if((details = login(userID,passwdHash)) != null)
						{
							out.println(details);
						}
						else
						{
							out.println("401");
						}
					}
					else if(line.equals("register"))
					{
						String newID = connectionRead.readLine();
						if(register(newID))
						{
							out.println("200");
						}
						else
						{
							out.println("500");
						}
					}
					else
					{
						out.println("400: BAD REQUEST!");
					}
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
	
	private static String login(String userID, String passwdHash)
	{
		//Search list of users for file called userID.rec
		//Check if hash inside userID.rec == passwdHash
		//If match, find file called userID.json and return contents
		//else return null;
		
		//Connect to S3 and define constants
		String bucket = "asx-user-store";
		String userCreds = "/creds/"+userID+".rec";
		String userData = "/data/"+userID+".json";
		
		AWSCredentials credentials = new BasicAWSCredentials(INSERT CREDS HERE);
		AmazonS3 s3Client = new AmazonS3Client(credentials);
		
		S3Object object = s3Client.getObject(new GetObjectRequest(bucket, userCreds));
		InputStream objectData = object.getObjectContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(objectData));
		try
		{
			String hash = reader.readLine();
			reader.close();
			objectData.close();
			if(hash.equals(passwdHash))
			{
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
			finally
			{
				return null;
			}
		}
	}
	
	private static boolean register(String userID)
	{
		return false;
	}
}
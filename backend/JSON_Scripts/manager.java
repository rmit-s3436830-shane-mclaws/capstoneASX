package com.amazonaws.samples;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class manager
{
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
			data = fileBuffer.readLine();
			String codes[] = data.split(",");
			/*****************************************************/
			
			//Work out number of threads required for download, each thread will handle 500 codes
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
				int upperLimit = (i + 1) * compPerThread - 1;
				if(upperLimit  > numCompanies)
				{
					upperLimit = numCompanies;
				}
				String[] subset = Arrays.copyOfRange(codes, i*compPerThread, upperLimit);
				threads[i] = new Thread(new downloader(subset));
				threads[i].start();
			}
			/*****************************************************/
		}
		catch (FileNotFoundException fnfe)
		{
			System.out.println("Error, CSV file not found!");
			fnfe.printStackTrace();
		}
		catch (IOException ie)
		{
			System.out.println("Exception thrown when reading CSV file!");
			ie.printStackTrace();
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
			}
		}
	}
}

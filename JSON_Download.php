<?php
	/***********************************************************************
	* This script will grab a list of trading shares from the ASX website. *
	* It will then grab the latest JSON file for each share and upload the *
	* JSON to the S3 bucket for later use. This script will be run every   *
	* 20 minutes during ASX trading hours (10AM - 5PM), as automated by    *
	* the EC2 OS the script will be hosted on.                             *
	***********************************************************************/
	
	//import library used to interface with S3
	require 'vendor/autoload.php';
	use Aws\S3\S3Client;
	/*****************************************************/
	
	//Access keys and defaults associated with S3 bucket
	//Keys saved in file so as to not be in plain text -HI GITHUB!!!!!-
	$keyFile = fopen("/home/ec2-user/ASX_JSON/rootkey.csv","r");
	$line = fgets($keyFile);
	$keys = explode(',',$line);
	$AWS_Access_Key = $keys[0];
	$AWS_Secret_Key = $keys[1];
	$AWS_Bucket_ID = 'asx-json-host';
	/*****************************************************/
	
	//Establish connection to S3 bucket
	$s3Client = S3Client::factory(array(
		'credentials' => array(
			'key' => $AWS_Access_Key,
			'secret' => $AWS_Secret_Key
		)
	));
	/*****************************************************/
	
	//Get current date & time
	date_default_timezone_set('Australia/Melbourne');
	$date = date('Ymd');
	$time = date('H:i');
	/*****************************************************/
	
	//Download an array of company trading codes to a local file at start of day
	if($time == "10:00")
	{
		$ASX_Company_List = fopen("http://www.asx.com.au/asx/research/ASXListedCompanies.csv", "r"); //Perma-link to most up to date CSV from ASX
		if($ASX_Company_List) //If remote file fails to open, skip this code block and proceed to using the current local file
		{
			$Codes = array();
			while(($line = fgets($ASX_Company_List)) != false)
			{
				$elements = explode(',', $line);
				//Check validity of line from file, if it contains a valid company entry
				if(count($elements) >= 3)
				{
					for($i=0; $i<count($elements); $i++)
					{
						if(strlen($elements[$i]) == 3) //Check if section of line contains the company trading code
						{
							array_push($Codes, $elements[$i]);
							break;
						}
					}
				}
			}
			fclose($ASX_Company_List);
			$Local_Company_List = fopen("/home/ec2-user/ASX_JSON/companies.csv","w");
			foreach($Codes as $code)
			{
				if($code == reset($Codes)) //First iteration
				{
					fwrite($Local_Company_List, $code);
				}
				else
				{
					fwrite($Local_Company_List, ",".$code);
				}
			}
			fclose($Local_Company_List);
		}
	}
	/*****************************************************/
	
	//Grab the array of company codes from the local file
	$Local_Company_List = fopen("/home/ec2-user/ASX_JSON/companies.csv", "r");
	if(!$Local_Company_List)
	{
		echo "Unable to open local file!\n";
		echo "Exiting...\n";
		exit;
	}
	$line = fgets($Local_Company_List);
	$Companies = explode(',', $line);
	fclose($Local_Company_List);
	
	/*****************************************************/
	
	//Grabs the latest JSON for each company listed and saves it to the S3 bucket
	//Also counts how many company entries were searched for
	$tries = 0;
	foreach($Companies as $company)
	{
		$tries++;
		//Retrieves the most up to date data from yahoo finance
		$tags = "nabl1t1c1p2ohgpwkjdqr1y"; //The different tags correspond to different attributes of the stock entry. Explanation can be found at: http://www.marketindex.com.au/yahoo-finance-api
		$dataURL = "http://finance.yahoo.com/d/quotes.csv?s=".$company.".AX&f=".$tags;
		$data = file_get_contents($dataURL);
		
		//Filename exists within folder 'ASX trading code', formatted as YYYYMMDD.json, example: CBA/20170313.json would be CBA on the 13th of March 2017
		$fileName = $company."/".$date.".json";	
		
		//Formats raw CSV into a readable format for alter use
		$entry = explode(',', $data);
		$entry = '{"Time":"'.$time.'","Name":'.$entry[0].',"ASX Code":"'.$company.'","Ask Price":"'.$entry[1].'","Bid Price":"'.$entry[2].'","Last Trade Price":"'.$entry[3].'","Last Trade Time":'.$entry[4].',"Change":"'.$entry[5].'","Change(%)":'.$entry[6].',"Opening Value":"'.$entry[7].'","Day High":"'.$entry[8].'","Day Low":"'.$entry[9].'","Previous Close":"'.$entry[10].'","52 Week Range":'.$entry[11].',"52 Week High":"'.$entry[12].'","52 Week Low":"'.$entry[13].'","Dividend/Share":"'.$entry[14].'","Ex-Dividend Date":"'.$entry[15].'","Dividende Pay Date":"'.$entry[16].'","Dividend Yield":"'.substr($entry[17],0,-1).'"}';
		
		if($time == "10:00") //If it's the first post of the day, create a new file and put the data in it
		{
			try
			{
				$s3Client->putObject(array(
					'Bucket' => $AWS_Bucket_ID,
					'Key' => $fileName,
					'Body' => $entry
				));
			}
			catch (S3Exception $e)
			{
				echo "Exception when creating new file\n";
				echo $e->getMessage();
				break;
			}
		}
		else //If the day's file exists, open it and append to the end
		{
			try
			{
				$result = $s3Client->getObject(array(
					'Bucket' => $AWS_Bucket_ID,
					'Key' => $fileName
					));
				$entry = $result['Body']."\n".$entry;
				try
				{
					$s3Client->putObject(array(
						'Bucket' => $AWS_Bucket_ID,
						'Key' => $fileName,
						'Body' => $entry
					));
				}
				catch (S3Exception $e)
				{
					echo "Exception when writing to existing file\n";
					echo $e->getMessage();
					break;
				}
			}
			catch (S3Exception $e)
			{
				echo "Exception when opening existing file\n";
				echo $e->getMessage();
				break;
			}
		}
		/*if($tries == 150) //This if will trigger once the first 150 entries have been grabbed, and then break out of the loop
		{ //Delete/comment this if block once debugging is complete and sprint is ready for presentation/review
			break; //Deleting/commenting this block will allow all ~2000 entries to be computed
		}*/
	}
	/*****************************************************/
?>
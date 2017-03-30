<?php
	/****************************************************************************
	* This script will grab the list of trading shares from the ASX website and	*
	* store the list locally, and int eh S3 bucket as a CSV file for later use. *
	* This script will be auto run daily at 10:05AM on weekdays. Sometimes new  *
	* company entites are added just after 10:00AM on trading days, but can		*
	* exist in the listing before then, and so to avoid the main download script*
	* trying to download trading info on a company that has no info yet, we 	*
	* update the local version of the comapny listing (companies.csv) after		*
	* baseline information has been added to yahoo finance. 5 mins should be	*
	* adequate time to allow for this to happen.								*
	****************************************************************************/
	
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
	
	//Grab latest copy of comapnies.csv, purge all urequired data, save as new csv file containing ASX trading codes
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
		
		//Open local copy company list and save contents to S3 bucket
		printf("creating ocmpany listing in S3\n");
		$S3_Company_List_Name = "companies.csv";
		$Local_Company_List = fopen("/home/ec2-user/ASX_JSON/companies.csv", "r");
		$line = fgets($Local_Company_List);
		try
		{
			$s3Client->putObject(array(
				'Bucket' => $AWS_Bucket_ID,
				'Key' => $S3_Company_List_Name,
				'Body' => $line
			));
		}
		catch (S3Exception $e)
		{
			echo "Exception when creating new file\n";
			echo $e->getMessage();
			break;
		}
		/*****************************************************/
	}
?>
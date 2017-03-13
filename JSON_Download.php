<?php
	/***********************************************************************
	* This script will grab a list of trading shares from the ASX website. *
	* It will then grab the latest JSON file for each share and upload the *
	* JSON to the S3 bucket for later use. This script will be run every   *
	* 20 minutes during ASX trading hours, as automated by the EC2 OS the  *
	* script will be hosted on.                                            *
	***********************************************************************/
	
	//Used for checking link validity
	function get_http_response_code($url)
	{
		$headers = get_headers($url);
		return substr($headers[0], 9, 3);
	}
	/*****************************************************/
	
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
	
	//Create an array of company trading codes
	$ASX_Company_List = fopen("http://www.asx.com.au/asx/research/ASXListedCompanies.csv", "r"); //Perma link to most up to date CSV from ASX
	if(!$ASX_Company_List)
	{
		//Exit if failed to retrieve csv from ASX
		echo "Unable to open remote file!\n";
		exit;
	}
	$Companies = array();
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
					array_push($Companies, $elements[$i]);
					break;
				}
			}
		}
	}
	//print_r($Companies);
	fclose($ASX_Company_List);
	/*****************************************************/
	
	//Grabs the latest JSON for each company listed and saves it to the S3 bucket
	//Also counts how many successes there were vs attempts
	$success = 0;
	$tries = 0;
	foreach($Companies as $company)
	{
		$tries++;
		//echo $company.": data point#".$tries;
		if(get_http_response_code('http://data.asx.com.au/data/1/share/'.$company.'/prices?interval=daily&count=1') == "200")
		{
			$success++;
			//echo " success#".$success;
			$entry = file_get_contents('http://data.asx.com.au/data/1/share/'.$company.'/prices?interval=daily&count=1');
			$fileName = $company."/".$date."-".$time.".json"; //Filename exists within folder 'ASX trading code', formatted as YYYYMMDD-HHMM.json, example: CBA/20170313-17:40.json would be CBA on the 13th of March 2017 at 5:40PM
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
				echo $e->getMessage();
			}
		}
		//echo "\n";
	}
	echo $success."/".$tries." trading codes were successful.\n";
	/*****************************************************/
?>

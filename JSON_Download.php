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
	$ASX_Company_List = fopen("http://www.asx.com.au/asx/research/ASXListedCompanies.csv", "r"); //Perma-link to most up to date CSV from ASX
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
	//Also counts how many company entries were searched for
	$tries = 0;
	foreach($Companies as $company)
	{
		$tries++;
			//echo $company.": data point#".$tries; //Used for debugging
		$tags = "nt1l1c1p2ohg"; //The different tags correspond to different attributes of the stock entry. Explanation can be found at: http://www.marketindex.com.au/yahoo-finance-api
		$dataURL = "http://finance.yahoo.com/d/quotes.csv?s=".$company.".AX&f=".$tags;
		$data = file_get_contents($dataURL);
		$fileName = $company."/".$date."-".$time.".json"; //Filename exists within folder 'ASX trading code', formatted as YYYYMMDD-HHMM.json, example: CBA/20170313-17:40.json would be CBA on the 13th of March 2017 at 5:40PM
		$entry = explode(',', $data);
		$entry = '{"Name":'.$entry[0].',"Last Trade Time":'.$entry[1].',"Last Trade Price":"'.$entry[2].'","Change":"'.$entry[3].'","Change(%)":'.$entry[4].',"Opening Value":"'.$entry[5].'","Day High":"'.$entry[6].'","Day Low":"'.substr($entry[7],0,-1).'"}'; //Formats raw CSV into a readable format
			//echo " Entry: ".$entry; //Used for debugging
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
		if($tries == 150) //This if will trigger once the first 150 entries have been grabbed, and then break out of the loop
		{ //Delete/comment this if block once debugging is complete and sprint is ready for presentation/review
			break; //Deleting/commenting this block will allow all ~2000 entries to be computed
		}
			echo "\n"; //Used for debugging
	}
		//echo "\n".$tries." trading codes attempted.\n"; // Used for debugging
	/*****************************************************/
?>

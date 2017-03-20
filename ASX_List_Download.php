<?php
	/****************************************************************************
	* This script will grab the list of trading shares from the ASX website and	*
	* store the lsit locally as a CSV file for later use. This script will be	*
	* auto run daily at 10:05AM on weekdays. Sometimes new company entites are	*
	* added just after 10:00AM on trading days, but can exist in the listing	*
	* before then, and so to avoid the main download script trying to download	*
	* trading info on a company that has no info yet, we update the local		*
	* version of the comapny listing (companies.csv) after baseline information *
	* has been added to yahoo finance. 5 mins should be adequate time to allow	*
	* for this to happen.														*
	****************************************************************************/
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
?>
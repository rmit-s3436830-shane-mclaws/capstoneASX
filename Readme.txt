The files/scripts in this folder are hosted on an Amazon ASW EC2 instance running CentOS.
They're scheduled, and run utilising the cron service.

ASX_List_Download.php is run every weekday at 10:05 AM.
This script downloads a list of ASX Trading codes from the ASX directly, and saves the list as a CSV on the EC2 instance, so it can be used in the JSON_Download.php script.

JSON_Download.php is run every 20 minutes between 10AM and 5PM on weekdays (ASX Trading hours).
This script facilitates the download and management of ASX share data, which is hosted on an AWS S3 instance for future utilisiation by the user fornt-end application.

UserServer.java is a server program that will always be running on the EC2 instance.
The user application will connect to it when a user is trying to login, or when registering a new account.
The server handles the validation of user login, and if successful, provides the user info file to the application so the suer can see their account details.
The server app will also handle new user registration.

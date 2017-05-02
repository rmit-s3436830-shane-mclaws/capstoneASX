The files/scripts in this folder are hosted on an Amazon ASW EC2 instance running CentOS.
They're scheduled, and run utilising the cron service.

JSON_Scripts/ASX_List_Download.php is run every weekday at 10:05 AM.
This script downloads a list of ASX Trading codes from the ASX directly, and saves the list as a CSV on the EC2 instance, so it can be used in the JSON_Download.php script.

JSON_Scripts/manager.java is run every 20 minutes between 10AM and 5PM on weekdays (ASX Trading hours).
This script facilitates the download and management of ASX share data, which is hosted on an AWS S3 instance for future utilisiation by the user front-end application. It uses threading, and download.java is the thread class that enables this.

User_Handlers/UserServer.java is a server program that will always be running on the EC2 instance.
The user application will connect to it when a user is trying to login, register, save, or display leaderboard.
Upon connection, UserServer will invoke a new thread, and send the user connection to the new thread to be handled.

User_Handlers/threadedConnection.java handles the incoming connections from the user application.
Fucntion details are given in 'Trading Wheels Backend Systems Documentation.docx'.

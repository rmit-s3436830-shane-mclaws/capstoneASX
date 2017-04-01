The files/scripts in this folder are hosted on an Amazon ASW EC2 instance running CentOS.
They're scheduled, and run utilising the cron service.

JSON_Scripts/ASX_List_Download.php is run every weekday at 10:05 AM.
This script downloads a list of ASX Trading codes from the ASX directly, and saves the list as a CSV on the EC2 instance, so it can be used in the JSON_Download.php script.

JSON_Scripts/JSON_Download.php is run every 20 minutes between 10AM and 5PM on weekdays (ASX Trading hours).
This script facilitates the download and management of ASX share data, which is hosted on an AWS S3 instance for future utilisiation by the user fornt-end application.

User_Handlers/UserServer.java is a server program that will always be running on the EC2 instance.
The user application will connect to it when a user is trying to login, register, save, or display leaderboard.
Upon connection, UserServer will invoke a new thread, and send the user connection to the new thread to be handled.

User_Handlers/threadedConnection.java handles the validation of user login, and if successful, provides the user info file to the application so the user can see their account details.
The server app will also handle new user registration, creating appropriate files on the S3 to track user data and validation.
When the user application attempts to save suerdata it will provde the server with the users emaila ddressa s an identifyer and the text to be populated in the users data file. The server will then replace the existing file with the new contents provided by the user application.
When the user application wants to view the leader board, the app will tell the server how many results it wants, and what position the highest of those results are. The server will then return the name, surname, and score of the people that fit this criteria on the leaderboard.

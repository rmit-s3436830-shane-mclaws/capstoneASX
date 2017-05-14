#!/bin/bash

kill -9 $(sudo lsof -ti tcp:28543)
sudo rm /home/ec2-user/ASX_Users/nohup.out
nohup java -jar /home/ec2-user/ASX_Users/UserServer.jar &

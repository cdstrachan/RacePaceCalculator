#!/usr/bin/env bash
echo 'Starting Spring Boot app'
cd '//home/ec2-user/target'

chmod 500 paceservices-0.0.4.jar
unlink /etc/init.d/pacec
sudo ln -s /home/ec2-user/target/paceservices-0.0.4.jar /etc/init.d/pacec
sudo service pacec start
Writes console logs to /var/log/<appname>.log
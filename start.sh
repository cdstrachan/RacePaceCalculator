#!/usr/bin/env bash
echo 'Starting Spring Boot app'

chmod 500 //home/ec2-user/pace/target/paceservices-0.0.5.jar
sudo ln -s /home/ec2-user/pace/target/paceservices-0.0.5.jar /etc/init.d/pacec
sudo service pacec start
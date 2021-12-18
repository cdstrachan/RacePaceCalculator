#!/usr/bin/env bash
# AWS CLI script to autodeploy to EC2
echo 'Starting Spring Boot app version 0.0.5'

chmod 500 //home/ec2-user/pace/target/paceservices-0.0.5.jar
sudo ln -s /home/ec2-user/pace/target/paceservices-0.0.5.jar /etc/init.d/pacec
sudo service pacec start
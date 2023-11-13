#!/usr/bin/env bash
# AWS CLI script to destroy in EC2
echo 'Stopping Spring Boot app'

sudo service pacec stop
unlink /etc/init.d/pacec
rm -rf /home/ec2-user/pace

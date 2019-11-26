#!/usr/bin/env bash
echo 'Stopping Spring Boot app'
cd '//home/ec2-user/target'

sudo service pacec stop
unlink /etc/init.d/pacec
rm paceservices-0.0.4.jar

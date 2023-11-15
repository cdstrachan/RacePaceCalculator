## Overview
This website creates pacing charts for distance running. It includes route profile as well as a fade as the runner gets tired towards the end of long runs.

Currently it supports the main races in Cape Town, but I am happy to add additional races (see generator below)

## Documentation
Download and run the app, it will launch a sprintboot app at http://localhost:80


## Templates
src/main/resource/templates
Add templates for your own races there, and ass the template to index.html

## Sample
Working sample here: http://onlinepacecharts.herokuapp.com/
if you wish to add a cource, create a pull request

## Template generator
https://github.com/cdstrachan/RacePaceCalculatorRouteGenerator
Simple CLI route generator

## Todo
(umm) unit tests and better error handling

## To Deploy
az spring app deploy --resource-group pace-calculator --service pace-calculator --name pacecalc1 --artifact-path target/paceservices-0.0.5.jar
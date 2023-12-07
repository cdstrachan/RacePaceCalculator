# THIS IS THE VERSION THAT WORKS!!!!!

## Ref
https://learn.microsoft.com/en-us/azure/developer/java/spring-framework/deploy-spring-boot-java-app-on-linux#create-the-spring-boot-on-docker-getting-started-web-app


## Local ARM/M1
docker build -t springboottest:latest .
docker run  -p 80:8080 springboottest  

## Remote
### Build for Linux AMD
docker buildx build --platform linux/amd64 -t pacecalc:latest -f Dockerfile .
### Deploy
docker login pacecalc.azurecr.io
docker tag pacecalc pacecalc.azurecr.io/pacecalc
docker push pacecalc.azurecr.io/pacecalc  


### Connect to SSH
docker exec -ti youthful_panini sh


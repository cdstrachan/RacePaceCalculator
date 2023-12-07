docker buildx build --platform linux/amd64 -t springboottest:latest -f Dockerfile .
docker tag springboottest containertestcs.azurecr.io/springboottest
docker push containertestcs.azurecr.io/springboottest  
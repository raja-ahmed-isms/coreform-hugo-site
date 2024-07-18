#!/bin/bash
docker image build --no-cache --build-arg SERVERPORT=8080 --build-arg MYSQLSERVER=host.docker.internal -t accounts .
docker stop accounts
docker rm accounts
docker run -d -p 8080:8080 --name accounts -v /mnt/csimsoft_downloads/tomcat55:/files accounts

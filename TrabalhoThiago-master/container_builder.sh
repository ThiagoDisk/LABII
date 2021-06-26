#!/bin/bash

mvn package -Pnative -Dquarkus.native.container-build=true

docker build -f src/main/docker/Dockerfile.native -t quarkus-quickstart/getting-started .
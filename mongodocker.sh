#!/bin/bash

# This script runs Mongo DB inside a Docker container.
# The image used is https://hub.docker.com/_/mongo/

SERVER_PORT=27777

# Pull MongoDB image (one-time only operation)
docker pull mongo

# Run Mongo daemon in detached mode in container named MONGOCONTAINER, publishing the chosen port.
# Automatically remove the container when it exits.
docker run --name MONGOCONTAINER --rm -d -p ${SERVER_PORT}:${SERVER_PORT} mongo mongod --port ${SERVER_PORT}

# Copy the JSON language sample data into the container
docker cp sampledata/language.json MONGOCONTAINER:/language.json

# Import the sample data into a new DB search_facade and collection language
docker exec MONGOCONTAINER mongoimport --port ${SERVER_PORT} --jsonArray --db search_facade --collection language --file language.json

read -n 1 -s -r -p "Press any key to stop and remove container. "

# To stop and remove container
docker stop MONGOCONTAINER >/dev/null
echo "Container stopped."

#!/bin/bash

# This script runs Mongo DB inside a Docker container.
# The image used is https://hub.docker.com/_/mongo/

MAPPED_MONGO_PORT=27777
MONGO_DEFAULT_PORT=27017

# Pull MongoDB image (one-time only operation)
docker pull mongo

# Run Mongo daemon in detached mode in container named mongocontainer, publishing the chosen port.
# Automatically remove the container when it exits.
docker run --name mongocontainer --rm -d -p ${MAPPED_MONGO_PORT}:${MONGO_DEFAULT_PORT} mongo

# Copy the JSON language sample data into the container
docker cp sampledata/language.json mongocontainer:/language.json

# Import the sample data into a new DB search_facade and collection language
docker exec mongocontainer mongoimport --jsonArray --db search_facade --collection language --file language.json

read -n 1 -s -r -p "Press any key to stop and remove container. "

# To stop and remove container
docker stop mongocontainer >/dev/null
echo "Container stopped."

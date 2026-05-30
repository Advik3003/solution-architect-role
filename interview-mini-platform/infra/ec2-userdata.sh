#!/bin/bash
set -euo pipefail

# User-data script for EC2 bootstrap.
# Interview point: immutable infrastructure can preload runtime dependencies.

sudo apt-get update -y
sudo apt-get install -y docker.io
sudo systemctl enable docker
sudo systemctl start docker

# Example app run command; replace with your image tag in real deployments.
sudo docker run -d --name account-service -p 8081:8081 interview/account-service:latest

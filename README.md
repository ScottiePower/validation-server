# validation-server

This is a simple spring boot application that will host a POST endpoint (/validate) on port 8080.

# Docker commands
- docker build -t zimmy71/validation-server:latest .
- docker run --publish 8080:8080 zimmy71/validation-server:latest
- docker push zimmy71/validation-server:latest
# validation-server

This is a simple spring boot application that will host a POST endpoint (/validate) on port 80.

# Docker commands
Docker files where created based off reference examples from
The multistage docker file will create a compact image, since the go tool chain will not be included.

- docker build -t zimmy71/validation-server:latest .
- docker push zimmy71/validation-server:latest
- docker run --publish 8080:8080 zimmy71/validation-server:latest
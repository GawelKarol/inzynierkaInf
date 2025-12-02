FROM ubuntu:latest
LABEL authors="doopa"

ENTRYPOINT ["top", "-b"]
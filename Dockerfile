FROM ubuntu:latest
LABEL authors="jakegodsall"

ENTRYPOINT ["top", "-b"]
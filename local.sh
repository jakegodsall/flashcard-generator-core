#!/bin/bash

# Use the correct Java version
sdk use java 21.0.7-tem

# Build the project
mvn install -Dmaven.javadoc.skip=true -Dgpg.skip=true
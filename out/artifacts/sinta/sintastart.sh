#!/bin/bash
cd "$(dirname "$(readlink -fn "$0")")"
java -jar sinta.jar 8081
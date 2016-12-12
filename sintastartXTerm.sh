#!/bin/bash
cd "$(dirname "$(readlink -fn "$0")")"
xterm -e java -jar sinta.jar 8081
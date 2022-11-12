#!/bin/bash

#Line that guarantees a case-unsensitive behaviour
uppercolor=$(echo $1 | tr '[:lower:]' '[:upper:]')

ant ragnabot2 -Dargs="$uppercolor $2 $3 $4"
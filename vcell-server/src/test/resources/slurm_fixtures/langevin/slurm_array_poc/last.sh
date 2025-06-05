#!/bin/bash

echo "LAST job start at $(date)" > last.output
sleep 2
echo "LAST job finished at $(date)" >> last.output
exit 0


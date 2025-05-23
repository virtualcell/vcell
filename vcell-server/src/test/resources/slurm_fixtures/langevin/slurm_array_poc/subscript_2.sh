#!/bin/bash

echo "job2 start at $(date)" > subscript_2.output
sleep 2
echo "job2 finished at $(date)" >> subscript_2.output
exit 99


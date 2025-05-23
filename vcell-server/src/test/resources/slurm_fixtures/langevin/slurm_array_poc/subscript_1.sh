#!/bin/bash

echo "job1 start at $(date)" > subscript_1.output
sleep 2
echo "job1 finished at $(date)" >> subscript_1.output
exit 0


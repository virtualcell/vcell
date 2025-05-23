#!/bin/bash

echo "job6 start at $(date)" > subscript_6.output
sleep 2
echo "job6 finished at $(date)" >> subscript_6.output
exit 130


#!/bin/bash

echo "job3 start at $(date)" > subscript_3.output
sleep 7
echo "job3 finished at $(date)" >> subscript_3.output
exit 0


#!/bin/bash

USERID=elastic
PSWD=password

CREDENTIALS="-u ${USERID}:${PSWD}"

# Print status of indices before removal
echo "----before trimming old records----"
curl --insecure -X GET ${CREDENTIALS} "https://localhost:9200/_cat/indices?v&pretty"

# Get the list of all metrics indices (including active and inactive metrics indices)
echo "----getting list of metrics indices----"
metrics_indices=$(curl -s --insecure -X GET ${CREDENTIALS} "https://localhost:9200/_cat/indices?v&pretty" | grep ".ds-metrics" | awk '{print $3}')

# drop the old_metrics_indices
for index in $metrics_indices
do
  echo "dropping index ${index} - if active, drop will fail"
  curl --insecure -X DELETE ${CREDENTIALS} "https://localhost:9200/${index}"
done

# print status of indices after removal
echo "----after trimming old records----"
curl --insecure -X GET ${CREDENTIALS} "https://localhost:9200/_cat/indices?v&pretty"

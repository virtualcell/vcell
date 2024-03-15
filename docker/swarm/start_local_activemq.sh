#!/bin/bash

docker run -d \
  --name activemqint \
  -p 61616:61616 \
  -p 8161:8161 \
  -e ACTIVEMQ_STATIC_QUEUES="simReq;dataReq;dbReq;simJob" \
  -e ACTIVEMQ_STATIC_TOPICS=clientStatus \
  -e ACTIVEMQ_MIN_MEMORY=512 \
  -e ACTIVEMQ_MAX_MEMORY=2048 \
  -e ACTIVEMQ_ENABLED_SCHEDULER=true \
  -e ACTIVEMQ_USERS_clientUser=dummy \
  -e ACTIVEMQ_GROUPS_reads=clientUser \
  -e ACTIVEMQ_GROUPS_writes=clientUser \
  -e ACTIVEMQ_CONFIG_AUTHENABLED=true \
  webcenter/activemq:5.14.3
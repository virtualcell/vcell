# install and configure the ELK stack on a single machine (without Docker).

## install ElasticSearch on elk.cam.uchc.edu, based on https://www.elastic.co/guide/en/elasticsearch/reference/current/rpm.html.  
(optional: to install using Docker, follow these instructions instead: https://www.elastic.co/guide/en/elasticsearch/reference/current/docker.html.)  

### check os and java versions (elk.cam.uchc.edu already had Java 1.8.0_141 and CentOS centos-release-7-3.1611.el7.centos.x86_64)

```bash
-bash-4.2$ rpm --query centos-release
-bash-4.2$ java -version
```

### install elastic rpm repository

```bash
rpm --import https://artifacts.elastic.co/GPG-KEY-elasticsearch
```

### create file in /etc/yum.repos.d/elasticsearch.repo with the following contents:

```
[elasticsearch-6.x]
name=Elasticsearch repository for 6.x packages
baseurl=https://artifacts.elastic.co/packages/6.x/yum
gpgcheck=1
gpgkey=https://artifacts.elastic.co/GPG-KEY-elasticsearch
enabled=1
autorefresh=1
type=rpm-md
```

### create and start service

```bash
sudo yum-complete-transaction
sudo yum install elasticsearch
sudo /bin/systemctl daemon-reload
sudo /bin/systemctl enable elasticsearch.service
sudo systemctl start elasticsearch.service
# sudo systemctl stop elasticsearch.service
```

### verify it worked by checking logs in /var/log/elasticsearch/

```bash
curl http://localhost:9200
```

returned

```
{
  "name" : "g7sJEpo",
  "cluster_name" : "elasticsearch",
  "cluster_uuid" : "GJQGCr7sRS-KOTdFb9YUNg",
  "version" : {
    "number" : "6.2.4",
    "build_hash" : "ccec39f",
    "build_date" : "2018-04-12T20:37:28.497551Z",
    "build_snapshot" : false,
    "lucene_version" : "7.2.1",
    "minimum_wire_compatibility_version" : "5.6.0",
    "minimum_index_compatibility_version" : "5.0.0"
  },
  "tagline" : "You Know, for Search"
}
```

### the default configuration is retained so far, for configuration instructions see https://www.elastic.co/guide/en/elasticsearch/reference/current/settings.html.

to apply overrides to elastic search systemd configuration (edits edits /etc/systemd/system/elasticsearch.service.d/override.conf):

```bash
sudo systemctl edit elasticsearch
```

```
[Service]
LimitMEMLOCK=infinity
```

then to apply changes

```bash
sudo systemctl daemon-reload
```

### disable swapping

disable swap on all Kubernetes nodes (per https://kubernetes.io/docs/setup/independent/install-kubeadm/)

```bash
sudo free -h
sudo blkid
sudo lsblk
sudo swapoff /dev/mapper/cl-swap
sudo free -h

# comment out swap line in fstab
sudo vi /etc/fstab

# reboot
sudo init 6
```

## installing logstash per https://www.elastic.co/guide/en/logstash/current/getting-started-with-logstash.html.  for a Docker install, see https://www.elastic.co/guide/en/logstash/current/docker.html


### create /etc/yum.repos.d/logstash.repo with the following contents:

```
[logstash-6.x]
name=Elastic repository for 6.x packages
baseurl=https://artifacts.elastic.co/packages/6.x/yum
gpgcheck=1
gpgkey=https://artifacts.elastic.co/GPG-KEY-elasticsearch
enabled=1
autorefresh=1
type=rpm-md
```

### to test

```bash
cd /usr/share/logstash
sudo bin/logstash -e 'input { stdin { } } output { stdout {} }'
# wait for prompt (20 seconds)
```

when the prompt "The stdin plugin is now waiting for input:" appears, enter "hello world"

```bash
hello world
{
      "@version" => "1",
          "host" => "elk.cam.uchc.edu",
       "message" => "hello world",
    "@timestamp" => 2018-06-10T15:35:15.198Z
}
# to exit issue CTRL-D
```

# install Kibana on Centos (https://www.elastic.co/guide/en/kibana/current/rpm.html).  Kibana loads its configuration from the /etc/kibana/kibana.yml file by default.

create a file /etc/yum.repos.d/kibana.repo with the following contents:

```
[kibana-6.x]
name=Kibana repository for 6.x packages
baseurl=https://artifacts.elastic.co/packages/6.x/yum
gpgcheck=1
gpgkey=https://artifacts.elastic.co/GPG-KEY-elasticsearch
enabled=1
autorefresh=1
type=rpm-md
```

```bash
sudo yum install kibana
sudo /bin/systemctl daemon-reload
sudo /bin/systemctl enable kibana.service
# Kibana can be started and stopped as follows:
sudo systemctl start kibana.service
# sudo systemctl stop kibana.service
```

configured Kibana to listen on public adapter (defaults to localhost).  /etc/kibana/kibana.yml and uncomment host and change from "localhost" to "elk.cam.uchc.edu".


# to configure this with my Docker Swarm clusters:

## monitoring without ELK ... without logs:  https://botleg.com/stories/monitoring-docker-swarm-with-cadvisor-influxdb-and-grafana/

## monitoring with ELK ... logging: https://botleg.com/stories/log-management-of-docker-swarm-with-elk-stack/

start the docker

```bash
sudo docker stack deploy -c docker-stack-logspout logspout
```

add a configuration file for the vcelldev pipeline in /etc/logstash/conf.d/vcelldev.conf 

```
input {
  udp {
    port  => 5000
    codec => json
  }
}

filter {
  if [docker][image] =~ /logstash/ {
    drop { }
  }
}

output {
  elasticsearch { hosts => ["localhost:9200"] }
  stdout { codec => rubydebug }
}
```

in kibana, add query filters such as:

```
{
  "query": {
    "wildcard": {
      "docker.name": "*vcellrel*"
    }
  }
}
```


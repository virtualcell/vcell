## Structured logging with Elastic Stack 8

### Overview

* application logging
  * produces log events with newline-delimited JSON (ndjson) according to the Elastic Common Schema
* ElasticSearch ingress (ElasticSearch, Kibana, Fleet)
  * the Fleet server, Kibana and ElasticSearch all run on a single machine.
  * each application server node has Elastic Agent installed, deployed and managed by the Fleet Server.
  * the Fleet **agent policy** specifies the following centrally manged integrations: 
    * "Docker Metrics & Logs"
    * "Elastic Agent"
    * "System"

### Application Logging

* JSON structured logging (newline delimited JSON) - one entry per newline
* Log4J2 with JsonTemplateLayout and a modified version of EcsLayout.json (Elastic Common Schema)
* fields are defined in vcell-util/src/main/resources/VCellEcsLayout.json).
* TODO (short term): use logging context to populate metadata (e.g. user, simid) during transactions
* TODO (longer term): introduce tracing context (e.g. OpenTelemetry) with trace/span ids added to logging context.

### Fleet configuration (for elastic agents)
1. **Elastic Agent installation** (start new elastic-agent with ```--insecure``` switch if the Fleet server uses an untrusted root cert.)

3. **"Docker Metrics & Logs" integration** (under "Collect Docker container logs" -> "Additional parsers configuration")
   ```yaml
   - ndjson:
       target: ""
       overwrite_keys: true
       add_error_key: true
   ```

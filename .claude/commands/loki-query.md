---
description: Query VCell logs from Loki via logcli, across prod/stage/dev (port-forwarded automatically)
---

Query the VCell Loki instance to investigate incidents, monitor failures, or trace user-reported errors. Same Loki, same tenant — choose the namespace per query.

The wrapper script handles installation, port-forwarding, and tenant headers.

## When invoked

The argument `$ARGUMENTS` is a free-form description of what to look for. Translate it into one or more `logcli query` invocations using the wrapper script:

```
bash tools/loki/loki-query.sh [logcli-args...] '<LogQL selector>'
```

The wrapper auto-runs `tools/loki/setup.sh` (idempotent — installs logcli if missing, starts a port-forward to `loki-read` if not already running, exports `LOKI_ADDR` and `LOKI_ORG_ID`).

If the user hasn't given specific time bounds, default to `--since=15m`. For incidents, narrow the window to ±5–10 min around the reported timestamp using `--from=<RFC3339Z>` and `--to=<RFC3339Z>` (Better Stack timestamps are HDT = UTC-10).

## Picking the namespace

Always include a `namespace=` label in every query. The same Loki instance covers all three VCell environments:

| `namespace=` | Site        | URL                          |
| ------------ | ----------- | ---------------------------- |
| `prod`       | Production  | `vcell.cam.uchc.edu`         |
| `stage`      | Staging     | `vcell-stage.cam.uchc.edu`   |
| `dev`        | Development | `vcell-dev.cam.uchc.edu`     |

If the user mentions a specific URL or "Better Stack incident", pick `prod` unless they say otherwise. If unsure, ask — don't guess across environments.

## VCell containers (per namespace)

Common to all three:

| `container=` | Service                                          |
| ------------ | ------------------------------------------------ |
| `api`        | Legacy Restlet `/api/v0/` (HealthService, BMDB)  |
| `data`       | SimDataServer - Data RPC consumer                |
| `db`         | Database RPC consumer                            |
| `sched`      | Simulation dispatcher                            |
| `submit`     | Sim submit / ServerJobDispatcher                 |
| `webapp`     | Angular webapp                                   |
| `export`     | Export server                                    |

Environment-specific:
- `prod` only: `rest` (Quarkus `/api/v1/`), `mongodb` (sidecar)
- `stage` only: `sif-prepull` (Apptainer SIF pre-build)

When in doubt, discover with `logcli series` (after `eval "$(bash tools/loki/setup.sh --quiet)"`):
```bash
logcli series --quiet --since=1h '{namespace="dev"}' | grep -oE 'container="[^"]+"' | sort -u
```

## Useful queries

```bash
# Recent ERRORs across all VCell pods in prod
bash tools/loki/loki-query.sh --since=1h \
  '{namespace="prod", container=~"api|data|db|sched|submit|rest"} |~ "ERROR"'

# Same sweep, but in dev (note: no `rest` container in dev)
bash tools/loki/loki-query.sh --since=1h \
  '{namespace="dev", container=~"api|data|db|sched|submit"} |~ "ERROR"'

# Around a specific incident, raw output for jq parsing
bash tools/loki/loki-query.sh --output=raw --limit=500 \
  --from="2026-05-05T14:15:00Z" --to="2026-05-05T14:25:00Z" \
  '{namespace="prod", container="data"} |~ "FailoverTransport|Exception"'

# Compare same container across two environments side-by-side
bash tools/loki/loki-query.sh --since=15m --output=raw \
  '{namespace=~"prod|stage", container="data"} |~ "ERROR"'

# Activity volume by minute (sanity that a container is even running)
bash tools/loki/loki-query.sh --output=raw --limit=20000 --since=1h \
  '{namespace="prod", container="data"}' \
  | jq -r '.["@timestamp"][:16] // empty' | sort | uniq -c
```

For raw-output queries, log lines are JSON; useful fields include `["@timestamp"]`, `log_level`, `["log.logger"]`, `["process.thread.name"]`, `message`. Pipe through `jq -r '...'` to extract a clean digest.

## Workflow

1. Read the user's request and identify: namespace (prod/stage/dev), time window, suspected container(s), keywords.
2. If the request is broad (e.g., "what's wrong with prod"), start with an ERROR-only sweep across all containers in that namespace, then drill in.
3. If the volume is large, save to `/tmp/<topic>.txt` with `--output=raw` and parse with `jq`.
4. Report the finding crisply: timestamp, namespace, thread/logger, key log lines. Cite the file:line in vcell source code if the stack frame is visible.
5. When done, leave the port-forward running (subsequent calls reuse it). If you want to stop it, run `bash tools/loki/teardown.sh`.

## First-time setup hints (for the user)

- Requires `kubectl` and a kubeconfig with cluster access. Default path is `~/.kube/kubeconfig_vxrails.yaml`; override via `LOKI_KUBECONFIG`.
- macOS-only auto-install of `logcli` via `brew install logcli`. On Linux, install manually before first use.
- Tenant is `uchc`; the script sets `X-Scope-OrgID` automatically.
- Bypasses the `loki-gateway` (whose ED25519 TLS cert LibreSSL/macOS curl can't handshake) by port-forwarding `loki-read` on plain HTTP.

User request: $ARGUMENTS


# Managed Biosim pipeline

## Command Overview

1. [upload OMEX files](#upload-omex-files)
2. [refresh status](#refresh-status)
3. [download runs](#download-runs)
4. [compare runs](#compare-runs)
5. [publish](#publish)

## Setup

create a .env file with the following:

```properties
CLIENT_ID=<<auth-provider-client-id>>
CLIENT_SECRET=<<auth-provider-client-secret>>
AUTH_HOST=auth.biosimulations.org
AUTH_AUDIENCE=api.biosimulations.org
API_BASE_URL=https://api.biosimulations.org|dev
RUN_APP_BASE_URL=https://run.biosimulations.org|dev

OMEX_SOURCE_DIR=<<path-to-source-omex-files>>
OMEX_OUTPUT_DIR=<<path-to-output-artifacts>>
```

## upload OMEX files

```bash
poetry run python main.py upload_omex --help
```

```
Usage: main.py upload_omex [OPTIONS]

  upload and run OMEX files at BioSimulations

Options:
  --simulator [tellurium|copasi|amici|vcell|pysces|libsbmlsim]
                                  simulator to run  [default: Simulator.vcell]
  --project-id TEXT               filter by project_id
  --omex-src-dir PATH             defaults env.OMEX_SOURCE_DIR
  --out-dir PATH                  defaults to env.OMEX_OUTPUT_DIR
  --help                          Show this message and exit.

```

## refresh status

```bash
poetry run python main.py refresh_status --help
```

```
Usage: main.py refresh_status [OPTIONS]

  fetch status of runs and update biosimulations_runs.ndjson

Options:
  --out-dir PATH  defaults to env.OMEX_OUTPUT_DIR
  --help          Show this message and exit.
```

## download runs

```bash
poetry run python main.py download_runs --help
```

```
Usage: main.py download_runs [OPTIONS]

  download runs (results.zip) from BioSimulations

Options:
  --omex-src-dir PATH             defaults env.OMEX_SOURCE_DIR
  --out-dir PATH                  defaults to env.OMEX_OUTPUT_DIR
  --project-id TEXT               filter by project_id
  --simulator [tellurium|copasi|amici|vcell|pysces|libsbmlsim]
                                  filter by simulator
  --help                          Show this message and exit.
```


## compare runs

```bash
poetry run python main.py compare_runs --help
```

```
Usage: main.py compare_runs [OPTIONS]

  compare downloaded runs

Options:
  --omex-src-dir PATH  defaults env.OMEX_SOURCE_DIR
  --out-dir PATH       defaults to env.OMEX_OUTPUT_DIR
  --project-id TEXT    filter by project_id
  --help               Show this message and exit.
```

## publish

```bash
poetry run python main.py publish --help
```

```
Usage: main.py publish [OPTIONS]

  publish validated projects to BioSimulations

Options:
  --omex-src-dir PATH  defaults env.OMEX_SOURCE_DIR
  --out-dir PATH       defaults to env.OMEX_OUTPUT_DIR
  --project-id TEXT    filter by project_id
  --help               Show this message and exit.
```

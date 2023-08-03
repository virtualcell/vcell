create a .env file with the following:

```properties
CLIENT_ID=<<auth-provider-client-id>>
CLIENT_SECRET=<<auth-provider-client-secret>>
AUTH_HOST=auth.biosimulations.org
AUTH_AUDIENCE=api.biosimulations.org
API_BASE_URL=https://api.biosimulations.org
RUN_APP_BASE_URL=https://run.biosimulations.org

OMEX_SOURCE_DIR=<<path-to-source-omex-files>>
OMEX_OUTPUT_DIR=<<path-to-output-artifacts>>
```

```bash
poetry run python biosimulations_pipeline/cli.py --help
```

```
    Usage: cli.py [OPTIONS] COMMAND [ARGS]...
    
    Options:
      --install-completion [bash|zsh|fish|powershell|pwsh]
                                      Install completion for the specified shell.
      --show-completion [bash|zsh|fish|powershell|pwsh]
                                      Show completion for the specified shell, to
                                      copy it or customize the installation.
      --help                          Show this message and exit.
    
    Commands:
      compare_runs    compare downloaded runs
      download_runs   download runs (results.zip) from BioSimulations
      publish         publish validated projects to BioSimulations
      refresh_status  fetch status of runs and update biosimulations_runs.ndjson
      upload_omex     upload and run OMEX files at BioSimulations
    
```

## upload OMEX files

```bash
poetry run python biosimulations_pipeline/cli.py upload_omex --help
```

```
Usage: cli.py upload_omex [OPTIONS]

  upload and run OMEX files at BioSimulations

Options:
  --simulator [tellurium|copasi|amici|vcell|pysces|libsbmlsim]
                                  simulator to run  [default: Simulator.vcell]
  --project-id TEXT               filter by project_id
  --omex-src-dir PATH             defaults env.OMEX_SOURCE_DIR
  --out-dir PATH                  defaults to env.OMEX_OUTPUT_DIR
  --help                          Show this message and exit.

```

## download runs

```bash
poetry run python biosimulations_pipeline/cli.py download_runs --help
```

```
Usage: cli.py download_runs [OPTIONS]

  download runs (results.zip) from BioSimulations

Options:
  --omex-src-dir PATH             defaults env.OMEX_SOURCE_DIR
  --out-dir PATH                  defaults to env.OMEX_OUTPUT_DIR
  --project-id TEXT               filter by project_id
  --simulator [tellurium|copasi|amici|vcell|pysces|libsbmlsim]
                                  filter by simulator
  --help                          Show this message and exit.
```

## refresh status

```bash
poetry run python biosimulations_pipeline/cli.py refresh_status --help
```

```
Usage: cli.py refresh_status [OPTIONS]

  fetch status of runs and update biosimulations_runs.ndjson

Options:
  --out-dir PATH  defaults to env.OMEX_OUTPUT_DIR
  --help          Show this message and exit.
```

## compare runs

```bash
poetry run python biosimulations_pipeline/cli.py compare_runs --help
```

```
Usage: cli.py compare_runs [OPTIONS]

  compare downloaded runs

Options:
  --omex-src-dir PATH  defaults env.OMEX_SOURCE_DIR
  --out-dir PATH       defaults to env.OMEX_OUTPUT_DIR
  --project-id TEXT    filter by project_id
  --help               Show this message and exit.
```

## publish

```bash
poetry run python biosimulations_pipeline/cli.py publish --help
```

```
Usage: cli.py publish [OPTIONS]

  publish validated projects to BioSimulations

Options:
  --omex-src-dir PATH  defaults env.OMEX_SOURCE_DIR
  --out-dir PATH       defaults to env.OMEX_OUTPUT_DIR
  --project-id TEXT    filter by project_id
  --help               Show this message and exit.
```

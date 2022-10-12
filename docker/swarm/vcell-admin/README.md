# VCell_CLI Utils

# To Build
install build tool **poetry**
```bash
pip install poetry
```
create local virtual environment and install package with dependencies
```bash
cd /vcell/vcell-vcellcli-utils
poetry install
```

# Run CLI in Poetry VM (get man page)
runs module from within virtual environment
```bash
cd /vcell/vcell-vcellcli-utils
poetry run python -m vcell_cli_utils.vcellcli
```

# Run as interactive python
when run as service from **org.vcell.cli.CLIUtils.java**, environment variable **cli.workingDir** is used as the install directory (e.g. /vcell/vcell-cli-utils)
```
cd /vcell/vcell-cli-utils
poetry run python -i -W ignore
>>> from vcell_cli_utils import wrapper
>>> command1
>>> command2
```


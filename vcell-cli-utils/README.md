# VCell_CLI Utils
[![PyPI version](https://badge.fury.io/py/vcell-cli-utils.svg)](https://badge.fury.io/py/vcell-cli-utils)

# To Build
install build tool **poetry**
```bash
pip install poetry
```
create local virtual environment and install package with dependencies
```bash
cd /vcell/vcell-cli-utils
poetry install
```

# Run CLI in Poetry VM (get man page)
runs module from within virtual environment
```bash
cd /vcell/vcell-cli-utils
poetry run python -m vcell_cli_utils.cli
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

### example command session
Caution: use of stdin/stdout to pipe commands/reponses to process.  This is sensitive to print statements or logging to stdout. 

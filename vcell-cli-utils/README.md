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
```python
>>> from vcell_cli_utils import wrapper
>>> wrapper.genStatusYaml("../../vcdb/published/biomodel/omex/sbml/biomodel_102061382.omex", "/tmp")
>>> wrapper.genStatusYaml("missing-file", "/tmp")
Traceback (most recent call last):
  File "<stdin>", line 1, in <module>
  File "/Users/schaff/Documents/workspace/vcell/vcell-cli-utils/vcell_cli_utils/wrapper.py", line 31, in genStatusYaml
    status.status_yml(omexFile, outDir)
  File "/Users/schaff/Documents/workspace/vcell/vcell-cli-utils/vcell_cli_utils/status.py", line 37, in status_yml
    for sedml in extract_omex_archive(omex_file):
  File "/Users/schaff/Documents/workspace/vcell/vcell-cli-utils/vcell_cli_utils/status.py", line 19, in extract_omex_archive
    raise FileNotFoundError("File does not exist: {}".format(omex_file))
FileNotFoundError: File does not exist: missing-file
```

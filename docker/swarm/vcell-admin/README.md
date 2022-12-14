# VCell admin CLI

# To Build
install build tool **poetry**
```bash
pip install poetry
```
create local virtual environment and install package with dependencies
```bash
cd /docker/swarm/vcell-admin
poetry install
```

# Run CLI in Poetry VM (get man page)
runs module from within virtual environment
```bash
cd /docker/swarm/vcell-admin
poetry run python -m vcell_admin.cli --help
```

```text
Usage: python -m vcell_admin.cli [OPTIONS] COMMAND [ARGS]...

Options:
  --install-completion [bash|zsh|fish|powershell|pwsh]
                                  Install completion for the specified shell.
  --show-completion [bash|zsh|fish|powershell|pwsh]
                                  Show completion for the specified shell, to
                                  copy it or customize the installation.
  --help                          Show this message and exit.

Commands:
  health     site status - as formerly queried by Nagios
  killjobs   kill simulation job (from a vcell-batch container)
  logjobs    show simulation job logs (from MongoDB)
  showjobs   show simulation jobs (using vcell database and slurm)
  slurmjobs  query slurm running jobs
```
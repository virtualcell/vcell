import typer

app = typer.Typer()

import os
from zipfile import ZipFile

import requests


@app.command("download_simulation_run", help="download full simulation run from Biosimulations.org")
def downloadSimulationRun(
        base_filename: str = typer.Argument(help="", show_default=False),
        run_id: str = typer.Argument(help="SimulationRun ID", show_default=False)
) -> None:
    api_base_url = os.environ.get('API_BASE_URL')
    r = requests.get(f'{api_base_url}/runs/{run_id}/download', allow_redirects=True)
    with open(f'{base_filename}.spec.omex', 'wb') as f:
        f.write(r.content)
    r = requests.get(f'{api_base_url}/logs/{run_id}', allow_redirects=True)
    with open(f'{base_filename}.logs.json', 'wb') as f:
        f.write(r.content)
    r = requests.get(f'{api_base_url}/results/{run_id}?includeData=true', allow_redirects=True)
    with open(f'{base_filename}.outputs.json', 'wb') as f:
        f.write(r.content)
    r = requests.get(f'{api_base_url}/results/{run_id}/download')
    with open(f'{base_filename}.results.zip', 'wb') as f:
        f.write(r.content)
    with ZipFile(f'{base_filename}.results.zip', 'r') as zip_ref:
        for zipInfo in zip_ref.filelist:
            if zipInfo.filename.endswith(".h5"):
                with open(f'{base_filename}.h5', 'wb') as f:
                    f.write(zip_ref.read(zipInfo))
            if zipInfo.filename.endswith(".pdf"):
                with open(f'{base_filename}.pdf', 'wb') as f:
                    f.write(zip_ref.read(zipInfo))
    os.remove(f'{base_filename}.results.zip')


if __name__ == "__main__":
    app()

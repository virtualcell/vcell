import os
import json
import fire
import requests
import urllib.request
from dotenv import load_dotenv
import http


def runProject(project_id, simulator='tellurium', omex_dir="./omex_files", biosimulations_runs_file = "biosimulations_runs.json", dev=False):
    """
    This function runs the project on biosimulations.
    """
    omex_file = os.path.join(omex_dir, project_id + ".omex")
    runAPI = "https://api.biosimulations.org/runs"
    if dev:
        runAPI = "https://api.biosimulations.dev/runs"
        

    simulation_run_data = {
        'name': project_id,
        'simulator': simulator,
        'version': 'latest',
    }

    multipart_form_data = {
        'file': (project_id + '.omex', open(omex_file, 'rb')),
        'simulationRun': (None, json.dumps(simulation_run_data)),

    }

    req = requests.post(
        runAPI,
        files=multipart_form_data)
    req.raise_for_status()
    res = req.json()
    simulation_id = res["id"]
    if(os.path.exists(biosimulations_runs_file)):
        with open(biosimulations_runs_file) as f:
            runs = json.load(f)
    else:
        runs = {}
    if project_id not in runs:
        runs[project_id] = []
    runs[project_id].append((simulator, simulation_id))

    with open(biosimulations_runs_file, 'w') as f:
        json.dump(runs, f)

    print("Ran " + project_id + " on biosimulations with simulation id: " + simulation_id)
    print("View:", runAPI.replace("api.", "run.") + "/" + simulation_id)

def get_token():
    load_dotenv()
    client_id = os.environ.get('CLIENT_ID')
    client_secret = os.environ.get('CLIENT_SECRET')

    conn = http.client.HTTPSConnection("auth.biosimulations.org")

    payload = f'{{"client_id":"{client_id}","client_secret":"{client_secret}","audience":"api.biosimulations.org","grant_type":"client_credentials"}}'

    headers = {'content-type': "application/json"}

    conn.request("POST", "/oauth/token", payload, headers)

    res = conn.getresponse()
    data = res.read()

    data = data.decode("utf-8")
    data = json.loads(data)
    token = data['access_token']
    token = "Bearer " + token
    return token


if __name__ == "__main__":
    fire.Fire(runProject)

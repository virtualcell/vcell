import http
import json
import os
from typing import BinaryIO, Union

import requests
from pydantic import BaseModel

from data_manager import DataManager
from datamodels import SourceOmex, Simulator, SimulationRun, BiosimulationsProject


class _SimulationRunApiRequest(BaseModel):
    name: str  # what does this correspond to?
    simulator: str
    simulatorVersion: str
    maxTime: int   # in minutes
    # email: Optional[str] = None
    # cpus: Optional[int] = None
    # memory: Optional[int] = None (in GB)


def check_run_status(simulation_run: SimulationRun) -> str:
    getrun = requests.get("https://api.biosimulations.org/runs/" + simulation_run.simulation_id)
    getrun_dict = getrun.json()
    result = getrun_dict['status']
    return result


def run_project(
        source_omex: SourceOmex,
        simulator: Simulator,
        data_manager: DataManager) -> None:
    """
    This function runs the project on biosimulations.
    """
    runAPI = str(os.environ.get('API_BASE_URL')) + '/runs'
    runAppBaseUrl = str(os.environ.get('RUN_APP_BASE_URL'))

    simulation_run_request = _SimulationRunApiRequest(
        name=source_omex.project_id,
        simulator=simulator,
        simulatorVersion='latest',
        maxTime=600,
    )

    print(source_omex.omex_file)
    with open(source_omex.omex_file, 'rb') as omex_file_handle:
        multipart_form_data: dict[str, Union[tuple[str, BinaryIO],  tuple[None, str]] = {
            'file': (source_omex.project_id + '.omex', omex_file_handle),
            'simulationRun': (None, simulation_run_request.json()),
        }
        req = requests.post(runAPI, files=multipart_form_data)
        req.raise_for_status()
        res = req.json()

    simulation_id = res["id"]
    """
    simulator: Simulator
    simulator_version: str
    simulation_id: str
    project_id: str
    status: Optional[str] = "Unknown"
    """
    data_manager.write_run(SimulationRun(
        simulator=simulator,
        simulator_version=res['simulatorVersion'],
        simulation_id=simulation_id,
        project_id=source_omex.project_id,
        status=res['status']
    ))

    print("Ran " + source_omex.project_id + " on biosimulations with simulation id: " + simulation_id)
    print("View:", runAppBaseUrl + "/runs/" + simulation_id)


def publish_project(data_manager: DataManager, run: SimulationRun, overwrite: bool = False) -> None:
    if run.status != "SUCCEEDED":
        print(run.project_id, "did not succeed - or status needs to be updated. status is", run.status)
        return

    for project in data_manager.read_projects():
        if project.project_id == run.project_id:
            print(run.project_id, "already published")
            return

    simulation_publish_data = {
        'id': run.project_id,
        'simulationRun': run.simulation_id,
    }
    token = get_token()
    headers = {
        "Authorization": f"{token}"
    }
    print(run.project_id, "publishing")
    getproj = requests.get("https://api.biosimulations.org/projects/" + run.project_id, headers=headers)
    if getproj.status_code == 404:
        req = requests.post(
            "https://api.biosimulations.org/projects/" + run.project_id,
            json=simulation_publish_data, headers=headers)
        req.raise_for_status()
        data_manager.write_project(BiosimulationsProject(
            project_id=run.project_id,
            simulation_id=run.simulation_id
        ))
    elif overwrite:
        req = requests.put(
            "https://api.biosimulations.org/projects/" + run.project_id,
            json=simulation_publish_data, headers=headers)
        req.raise_for_status()
        data_manager.write_project(BiosimulationsProject(
            project_id=run.project_id,
            simulation_id=run.simulation_id
        ))


def get_token() -> str:
    client_id = os.environ.get('CLIENT_ID', "CLIENT_ID-not-set")
    client_secret = os.environ.get('CLIENT_SECRET', "CLIENT_SECRET-not-set")
    auth_host = os.environ.get('AUTH_HOST', "AUTH_HOST-not-set")
    auth_audience = os.environ.get('AUTH_AUDIENCE', "AUTH_AUDIENCE-not-set")

    conn = http.client.HTTPSConnection(auth_host)
    payload = f'{{"client_id":"{client_id}","client_secret":"{client_secret}",' \
              f'"audience":"{auth_audience}","grant_type":"client_credentials"}}'
    headers = {'content-type': "application/json"}
    conn.request("POST", "/oauth/token", payload, headers)

    res = conn.getresponse()
    data_bytes = res.read()

    data = data_bytes.decode("utf-8")
    data_dict = json.loads(data)
    token: str = data_dict['access_token']
    token = "Bearer " + token
    return token



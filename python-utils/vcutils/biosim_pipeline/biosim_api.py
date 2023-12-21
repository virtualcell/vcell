import http
import json
import os
from typing import BinaryIO, Union

import requests
from requests import Response
from pydantic import BaseModel

from vcutils.biosim_pipeline.data_manager import DataManager
from vcutils.biosim_pipeline.datamodels import SourceOmex, Simulator, SimulationRun, BiosimulationsProject


class _SimulationRunApiRequest(BaseModel):
    name: str  # what does this correspond to?
    simulator: str
    simulatorVersion: str
    maxTime: int  # in minutes
    # email: Optional[str] = None
    # cpus: Optional[int] = None
    # memory: Optional[int] = None (in GB)


def check_run_status(simulation_run: SimulationRun) -> str:
    """
    This function retrieves the status of a simulation run sent to RunBiosimulations
    """
    base_url = str(os.environ.get('API_BASE_URL'))
    get_run: Response = requests.get(f"{base_url}/runs/" + simulation_run.simulation_id)
    get_run_dict: dict = get_run.json()
    result: str = get_run_dict['status']
    return result


def run_project(source_omex: SourceOmex, simulator: Simulator, data_manager: DataManager) -> None:
    """
    This function runs the project on RunBiosimulations.
    """
    run_api: str = str(os.environ.get('API_BASE_URL')) + '/runs'
    run_app_base_url: str = str(os.environ.get('RUN_APP_BASE_URL'))

    simulation_run_request: _SimulationRunApiRequest = _SimulationRunApiRequest(
        name=source_omex.project_id,
        simulator=simulator,
        simulatorVersion='latest',
        maxTime=600,
    )

    print(source_omex.omex_file_path)
    with open(source_omex.omex_file_path, 'rb') as omex_file_handle:
        multipart_form_data: dict[str, Union[tuple[str, BinaryIO], tuple[None, str]]] = {
            'file': (source_omex.project_id + '.omex', omex_file_handle),
            'simulationRun': (None, simulation_run_request.json()),
        }
        req = requests.post(run_api, files=multipart_form_data)
        req.raise_for_status()
        res = req.json()

    simulator_version: str = res['simulatorVersion']
    simulation_id: str = res["id"]
    status: str = res['status']
    data_manager.write_run(SimulationRun(
        simulator=simulator,  # simulator: Simulator
        simulator_version=simulator_version,  # simulator_version: str
        simulation_id=simulation_id,  # simulation_id: str
        project_id=source_omex.project_id,  # project_id: str
        status=status  # status: Optional[str] = "Unknown"
    ))

    print("Ran " + source_omex.project_id + " on biosimulations with simulation id: " + simulation_id)
    print("View:", run_app_base_url + "/runs/" + simulation_id)


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
    base_url = str(os.environ.get('API_BASE_URL'))
    get_proj: Response = requests.get(f"{base_url}/projects/" + run.project_id, headers=headers)
    if get_proj.status_code == 404:
        req = requests.post(
            f"{base_url}/projects/" + run.project_id,
            json=simulation_publish_data, headers=headers)
        req.raise_for_status()
        data_manager.write_project(BiosimulationsProject(
            project_id=run.project_id,
            simulation_id=run.simulation_id
        ))
    elif overwrite:
        req = requests.put(
            f"{base_url}/projects/" + run.project_id,
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


def get_projects(*args: str) -> list[dict]:
    base_url: str = str(os.environ.get('API_BASE_URL'))
    project_query: str = f'{base_url}/projects/'
    projects: list[dict] = requests.get(project_query).json()
    if len(args) < 1:
        return projects
    arg_set = set(args)
    projects_to_pull: list[dict] = []
    for project in projects:
        project_id: str = project["id"]
        if project_id not in arg_set:
            continue
        projects_to_pull.append(project)

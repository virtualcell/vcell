# -*- coding: utf-8 -*-
"""
Created on Tue Aug  2 10:07:17 2022

@author: Lucian
"""

import csv
import os
import json
import requests
import re
from run_project_on_biosimulations import runProject, get_token
#from biosimulators_utils.biosimulations.utils import publish_simulation_project

def getRunID(biomdID, rsim, request_runs):
    ret = None
    if biomdID in request_runs:
        #Get the last one that matches:
        for (sim, runID) in request_runs[biomdID]:
            if sim==rsim:
                ret = runID
    return ret


biosimulations_requests_file = "biosimulations_requests.json"
if(os.path.exists(biosimulations_requests_file)):
    with open(biosimulations_requests_file) as f:
        reqs = json.load(f)
else:
    reqs = {}


biosimulations_request_runs_file = "biosimulations_request_runs.json"
if(os.path.exists(biosimulations_request_runs_file)):
    with open(biosimulations_request_runs_file) as f:
        request_runs = json.load(f)
else:
    request_runs = {}

token = get_token()
headers = {
    "Authorization": f"{token}"
}


for biomdID in reqs:
    sim = reqs[biomdID]
    runID = getRunID(biomdID, sim, request_runs)
    if runID:
        getrun = requests.get("https://api.biosimulations.org/runs/" + runID, headers=headers)
        getrun = getrun.json()
        result = getrun['status']
        if result == "FAILED":
            print(biomdID, sim, "did not succeed. Result is", result)
            runProject(biomdID, simulator=sim, biosimulations_runs_file="biosimulations_request_runs.json")
        elif result == "SUCCEEDED":
            pass
        else:
            print(biomdID, sim, result, runID)
    else:
        runProject(biomdID, simulator=sim, biosimulations_runs_file="biosimulations_request_runs.json")
    

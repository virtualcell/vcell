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

biosimulations_projects_file = "biosimulations_projects.json"
if(os.path.exists(biosimulations_projects_file)):
    with open(biosimulations_projects_file) as f:
        projects = json.load(f)
else:
    projects = {}

proj_csv = open("biosimulations_projects.csv", "w")
proj_csv.write("Biomodel,projid,simulator,runid\n")

for biomd in projects:
    (projid, runid) = projects[biomd]
    proj_csv.write(biomd)
    proj_csv.write(",")
    proj_csv.write(projid)
    proj_csv.write(",")
    proj_csv.write(projid.split("_")[1])
    proj_csv.write(",")
    proj_csv.write(runid)
    proj_csv.write(",")
    proj_csv.write("\n")

proj_csv.close()
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
from run_project_on_biosimulations import get_token
#from biosimulators_utils.biosimulations.utils import publish_simulation_project


counter = {}
def pickOne(validated):
    validated = tuple(validated)
    if validated not in counter:
        counter[validated] = 0
    ret = validated[counter[validated]]
    counter[validated] += 1
    if counter[validated] >= len(validated):
        counter[validated] = 0
    return ret

biosimulations_runs_file = "biosimulations_runs.json"
if(os.path.exists(biosimulations_runs_file)):
    with open(biosimulations_runs_file) as f:
        runs = json.load(f)
else:
    runs = {}

biosimulations_projects_file = "biosimulations_projects.json"
if(os.path.exists(biosimulations_projects_file)):
    with open(biosimulations_projects_file) as f:
        projects = json.load(f)
else:
    projects = {}
    
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

def getRunID(biomdID, rsim, request_runs):
    ret = None
    if biomdID in request_runs:
        #Get the last one that matches:
        for (sim, runID) in request_runs[biomdID]:
            if sim==rsim:
                ret = runID
    return ret

def publish(biomodel, sim, token, overwrite = False):
    if (not overwrite) and biomodel in projects:
        return
    if biomodel in projects:
        (projID, runID) = projects[biomodel]
        sim = projID.split("_")[1]
        
    # if biomodel == "BIOMD0000000960":
    #     return
        
    runID = getRunID(biomodel, sim, request_runs)

    if not runID:
        biomod_runs = runs[biomodel]
        for (this_sim, this_runid) in biomod_runs:
            if this_sim==sim:
                runID = this_runid
                break
    print(runID)
    #The status lies, so don't actually use it:
    getrun = requests.get("https://api.biosimulations.org/runs/" + runID)
    getrun = getrun.json()
    result = getrun['status']
    if result != "SUCCEEDED":
        print(biomodel, sim, "did not succeed. Result is", result)
        reqs[biomodel] = sim
        return
    summary = requests.get("https://api.biosimulations.org/runs/" + runID + "/summary")
    summary = summary.json()
    title = summary['metadata'][0]['title']
    title = re.sub('[^0-9a-zA-Z_]+', '_', title)
    projID = biomodel + "_" + sim + "_" + title[:30]
    simulation_publish_data = {
        'id': projID,
        'simulationRun': runID,
    }
    headers = {
        "Authorization": f"{token}"
    }
    print(projID)
    projects[biomodel] = [projID, runID]
    # getlogin = requests.get("https://api.biosimulations.org/auth/loggedIn", headers=headers)
    # getlogin = getlogin.json()
    # publish_simulation_project(runID, projID, auth=token)
    if biomodel in ["BIOMD0000000001", "BIOMD0000000002"]:
        return
    getproj = requests.get("https://api.biosimulations.org/projects/" + projID, headers=headers)
    if getproj.status_code == 404:
        req = requests.post(
            "https://api.biosimulations.org/projects/" + projID,
            json=simulation_publish_data, headers=headers)
        req.raise_for_status()
    elif overwrite:
        req = requests.put(
            "https://api.biosimulations.org/projects/" + projID,
            json=simulation_publish_data, headers=headers)
        req.raise_for_status()


token = get_token()
csvfile = open("sim_compare.csv", "r")
csv_reader = csv.DictReader(csvfile)
sims = ['vcell', 'amici', 'copasi', 'tellurium', 'pysces', 'libsbmlsim']
chosenCount = {}
for sim in sims:
    chosenCount[sim] = 0
for row in csv_reader:
    if row["amici"] == "amici":
        continue
    biomodel = row['Biomodel']

    validated = []
    for sim in sims:
        if "; validated" in row[sim]:
            validated.append(sim)
    if len(validated)==0:
        print(biomodel, "Nothing validated")
    else:
        chosen = pickOne(validated)
        chosenCount[chosen] += 1
        print(biomodel, chosen)
        publish(biomodel, chosen, token)


csvfile.close()

print(chosenCount)
with open(biosimulations_projects_file, 'w') as f:
    json.dump(projects, f)

with open(biosimulations_requests_file, 'w') as f:
    json.dump(reqs, f)

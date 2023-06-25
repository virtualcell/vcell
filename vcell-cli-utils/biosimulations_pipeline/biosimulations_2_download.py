# -*- coding: utf-8 -*-
"""
Created on Tue Aug  2 10:07:17 2022

@author: Lucian
"""

import os
import urllib
import json
import requests
import urllib.request


def getProjectData(biosimulations_runs_file = "biosimulations_runs.json", project_id=None, simulator=None, output_dir="biosimulations_output", skip_existing = True):
    if(os.path.exists(biosimulations_runs_file)):
        with open(biosimulations_runs_file) as f:
            runs = json.load(f)
    else:
        runs = {}
    
    if not os.path.exists(output_dir):
        os.mkdir(output_dir)
    
    for run in runs:
        if project_id and project_id != run:
            continue
        print("Retrieving", run)
        rundir = os.path.join(output_dir, run)
        if not os.path.exists(rundir):
            os.mkdir(rundir)
        for (sim, id) in runs[run]:
            if simulator and sim != simulator:
                continue
            print("...from", sim)

            #The status lies, so don't actually use it:
            # getrun = requests.get("https://api.biosimulations.org/runs/" + id)
            # getrun = getrun.json()
            # result = getrun['status']
            # if result != "SUCCEEDED":
            #     print(run, sim, "did not succeed. Result is", result)
            #     continue
        

            simdir = os.path.join(rundir, sim)
            if not os.path.exists(simdir):
                os.mkdir(simdir)
            if skip_existing and os.path.exists(os.path.join(simdir, "results.zip")):
                print("Already have data")
                continue
            try:
                urllib.request.urlretrieve("https://api.biosimulations.org/results/" + id + "/download", simdir + "/results.zip")
            except urllib.error.HTTPError as e:
                print("Failure:", e)


#getProjectData(project_id="BIOMD0000000235")
getProjectData()

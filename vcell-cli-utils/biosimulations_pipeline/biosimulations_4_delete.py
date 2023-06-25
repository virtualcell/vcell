# -*- coding: utf-8 -*-
"""
Created on Tue Aug  2 10:07:17 2022

@author: Lucian
"""

import os
import urllib
import json
from zipfile import ZipFile
import h5py
import requests
from run_project_on_biosimulations import get_token

def deleteRuns(biosimulations_runs_file = "biosimulations_runs_old.json", project_id=None, simulator=None, token=None):
    if(os.path.exists(biosimulations_runs_file)):
        with open(biosimulations_runs_file) as f:
            runs = json.load(f)
    else:
        runs = {}
    
    for run in runs:
        if project_id and project_id != run:
            continue
        for (sim, id) in runs[run]:
            if simulator and sim != simulator:
                continue
            delrun = requests.delete("https://api.biosimulations.org/runs/" + id)
            delrun.raise_for_status()

            delrun = delrun.json()


token = get_token()
deleteRuns(token=token)

# -*- coding: utf-8 -*-
"""
Created on Tue Aug  2 10:07:17 2022

@author: Lucian
"""

import os
import json
from run_project_on_biosimulations import runProject

biosimulations_projects_file = "biosimulations_projects.json"
if(os.path.exists(biosimulations_projects_file)):
    with open(biosimulations_projects_file) as f:
        projects = json.load(f)
else:
    projects = {}

#Fire things off to Biosimulations:
for biomdnum in range(0, 1070):
    if biomdnum in [505]:
        #File too large.
        continue
    biomdID = "BIOMD000000" + str(biomdnum).zfill(4)
    if not os.path.exists("final/" + biomdID):
        #The biomodel doesn't exist (number skipped or something)
        continue
    if biomdID in projects:
        #The biomodel is already validated and published
        continue
    # runProject(biomdID, simulator="tellurium", biosimulations_runs_file = "biosimulations_runs.json")
    # runProject(biomdID, simulator="copasi", biosimulations_runs_file = "biosimulations_runs.json")
    # runProject(biomdID, simulator="amici", biosimulations_runs_file = "biosimulations_runs.json")
    runProject(biomdID, simulator="vcell", biosimulations_runs_file = "biosimulations_runs.json")
    runProject(biomdID, simulator="pysces", biosimulations_runs_file = "biosimulations_runs.json")
    runProject(biomdID, simulator="libsbmlsim", biosimulations_runs_file = "biosimulations_runs.json")
    
    
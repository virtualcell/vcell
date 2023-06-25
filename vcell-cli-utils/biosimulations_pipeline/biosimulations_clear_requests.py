# -*- coding: utf-8 -*-
"""
Created on Wed Jun  7 12:08:02 2023

@author: Lucian
"""

import csv
import os
import json
import requests
import re
from run_project_on_biosimulations import get_token


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

for entry in projects:
    if entry in reqs:
        del reqs[entry]
        print("Found", entry)

with open(biosimulations_requests_file, 'w') as f:
    json.dump(reqs, f)

        
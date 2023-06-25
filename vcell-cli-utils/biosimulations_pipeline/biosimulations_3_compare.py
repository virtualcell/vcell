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
import numpy as np
import copy
import requests
from matplotlib import pyplot as plt


biosimulations_runs_file = "biosimulations_runs.json"
if(os.path.exists(biosimulations_runs_file)):
    with open(biosimulations_runs_file) as f:
        runs = json.load(f)
else:
    runs = {}

biosimulations_runs_file = "biosimulations_runs_round2.json"
if(os.path.exists(biosimulations_runs_file)):
    with open(biosimulations_runs_file) as f:
        newruns = json.load(f)
for run in newruns:
    if run not in runs:
        runs[run] = newruns[run]
        
biosimulations_runs_file = "biosimulations_runs_round1.json"
if(os.path.exists(biosimulations_runs_file)):
    with open(biosimulations_runs_file) as f:
        newruns = json.load(f)
for run in newruns:
    if run not in runs:
        runs[run] = newruns[run]
        


ds_dict = {}  
def get_ds_dictionaries(name, node):
    #From https://stackoverflow.com/questions/70055365/hdf5-file-to-dictionary
    fullname = node.name
    if isinstance(node, h5py.Dataset):
    # node is a dataset
        # print(f'Dataset: {fullname}; adding to dictionary')
        ds_dict[fullname] = np.array(node)
        # print('ds_dict size', len(ds_dict)) 
    # else:
     # node is a group
        # print(f'Group: {fullname}; skipping')  
    
def getRunID(biomdID, rsim, runs):
    ret = None
    if biomdID in runs:
        #Get the first one that matches:
        for (sim, runID) in runs[biomdID]:
            if sim==rsim:
                return runID
    return ret

def compareArrays(arr1, arr2):
    if type(arr1[0]) == np.float64:
        max1 = max(arr1)
        max2 = max(arr2)
        atol = max(1e-3, max1*1e-5, max2*1e-5)
        return np.allclose(arr1, arr2, rtol=1e-4, atol=atol)
    for n in range(len(arr1)):
        if not compareArrays(arr1[n], arr2[n]):
            return False
    return True


def compareResults(results, biomodel):
    comparisons = {}
    validations = {}
    usedkeys = []
    for sim1 in results:
        runID = getRunID(biomodel.split("\\")[-1], sim1, runs)
    
        if not runID:
            validations[sim1] = "Unknown"
        else:
            #The status lies, so don't actually use it:
            getrun = requests.get("https://api.biosimulations.org/runs/" + runID)
            getrun = getrun.json()
            validations[sim1] = getrun['status']
    for sim1 in results:
        usedkeys.append(sim1)
        for sim2 in results:
            if sim2 in usedkeys:
                continue
            for f in results[sim1]:
                if f not in results[sim2]:
                    comparisons[(sim1, sim2)] = "Failed:  file " + f + " in " + sim1 + " but not in " + sim2
                    continue
                else:
                    for report in results[sim1][f]:
                        if report not in results[sim2][f]:
                            comparisons[(sim1, sim2)] = "Failed:  report " + report + " in " + sim1 + " but not in " + sim2
                    
            for f in results[sim2]:
                if f not in results[sim1]:
                    comparisons[(sim1, sim2)] = "Failed:  file " + f + " in " + sim2 + " but not in " + sim1
                else:
                    for report in results[sim2][f]:
                        if report not in results[sim1][f]:
                            comparisons[(sim1, sim2)] = "Failed:  report " + report + " in " + sim2 + " but not in " + sim1
            if (sim1, sim2) in comparisons:
                continue
            for f in results[sim1]:
                for report in results[sim1][f]:
                    arr1 = results[sim1][f][report]
                    arr2 = results[sim2][f][report]
                    if arr1.shape != arr2.shape:
                        # print("Different data dimensions for", sim1, sim2, report)
                        comparisons[(sim1, sim2)] = "Failed:  data different dimensions in report " + report
                    else:
                        if not compareArrays(arr1, arr2):
                            comparisons[(sim1, sim2)] = "Failed:  numpy reports divergence in report " + report
                            continue
                                
            if (sim1, sim2) not in comparisons:
                comparisons[(sim1, sim2)] = "Success"
                if "validated" not in validations[sim1]:
                    validations[sim1] += "; validated"
                if "validated" not in validations[sim2]:
                    validations[sim2] += "; validated"
    return comparisons, validations
            
                    

def compareDirectory(simdir):
    results = {}
    for root, dirs, files in os.walk(simdir):
        for dir in dirs:
            results[dir] = {}
        for file in files:
            if file == "results.zip":
                thezip = ZipFile(os.path.join(root, file), "r")
                for name in thezip.namelist():
                    if "reports.h5" in name:
                        # reports = thezip.open(name)
                        # for line in reports:
                        #     if b"autogen" in line:
                        #         print(line)
                        # reports.close()
                        h5 = h5py.File(thezip.open(name))
                        h5.visititems(get_ds_dictionaries)
                        results[os.path.basename(root)][name] = copy.deepcopy(ds_dict)
                        ds_dict.clear()
    print("Comparing results for", simdir)
    return compareResults(results, simdir)
    
def compareAll(output_dir="biosimulations_output_combined"):
    results = {}
    for root, dirs, files in os.walk(output_dir):
        for dir in dirs:
            if "BIOMD" in dir:
                results[dir] = compareDirectory(os.path.join(root, dir))
    return results


results = compareAll()
# print(results)

compareList = set()
simlist = ["amici", "tellurium", "copasi", "vcell", "pysces", "libsbmlsim"]

for biomd in results:
    (comparisons, validations) = results[biomd]
    for simpair in comparisons:
        (sim1, sim2) = simpair
        if sim1 not in simlist:
            continue
        if sim2 not in simlist:
            continue
        compareList.add(simpair)

compareList = list(compareList)
compareList.sort()

outfile = open("sim_compare.csv", "w")
outfile.write("Biomodel,")
for sim in simlist:
    outfile.write(sim)
    outfile.write(",")
   
for (sim1, sim2) in compareList:
    outfile.write(sim1 + "/" + sim2)
    outfile.write(",")
outfile.write("\n")
for biomd in results:
    outfile.write(biomd)
    outfile.write(",")
    (comparisons, validations) = results[biomd]
    simvalid = set()
    for sim in simlist:
        if sim in validations:
            outfile.write(validations[sim])
            outfile.write(",")
            if "validated" in validations[sim]:
                simvalid.add(sim)
        else:
            outfile.write("Missing,")
    for simpair in compareList:
        if simpair in comparisons:
            outfile.write(str(comparisons[simpair]))
            outfile.write(",")
            if simpair[0] in simvalid and simpair[1] in simvalid and "Success" not in comparisons[simpair]:
                print(biomd, ":", simpair, "both validated, but don't match.")
                # assert(False)
        else:
            outfile.write("[missing],")
    outfile.write("\n")

outfile.close()
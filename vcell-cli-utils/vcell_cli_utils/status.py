import os
from os.path import basename
import fire
from biosimulators_utils.archive.io import ArchiveReader
from biosimulators_utils.log.data_model import TaskLog
import libsedml
import yaml
import tempfile
import zipfile
import shutil
import json

# Create temp directory
tmp_dir = tempfile.mkdtemp()

def extract_omex_archive(omex_file):
    if not os.path.isfile(omex_file):
        raise FileNotFoundError("File does not exist: {}".format(omex_file))

    if not zipfile.is_zipfile(omex_file):
        raise IOError("File is not an OMEX Combine Archive in zip format: {}".format(omex_file))

    archive_reader = ArchiveReader()
    omex_metadata_tuple = archive_reader.run(omex_file, tmp_dir).to_tuple()

    sedml_files_list = list()
    for sed in omex_metadata_tuple[0]:
        if sed[1].endswith(".sedml"):
            sedml_files_list.append(sed[1])

    return sedml_files_list


def status_yml(omex_file: str, out_dir: str):
    yaml_dict = []
    for sedml in extract_omex_archive(omex_file):
        outputs_dict = {"outputs": []}
        tasks_dict = {"tasks": []}
        # add temp dir path
        sedml_path = os.path.join(tmp_dir, sedml)
        sedml_doc = libsedml.readSedMLFromFile(sedml_path)

        # Get all the required metadata
        tasks = sedml_doc.getListOfTasks()
        plots = sedml_doc.getListOfOutputs()

        # Convert into the list
        task_list = [task.getId() for task in tasks]
        plots_dict = {}
        reports_dict = {}
        other_list = []

        for plot in plots:
            if type(plot) == libsedml.SedPlot2D:
                plots_dict[plot.getId()] = [curve.getId() for curve in plot.getListOfCurves()]
            elif type(plot) == libsedml.SedReport:
                reports_dict[plot.getId()] = [dataSet.getId() for dataSet in plot.getListOfDataSets()]
            else:
                other_list.append(plot.getId())

        for plot in list(plots_dict.keys()):
            curves_list = []
            for curve in plots_dict[plot]:
                curves_list.append({"id":curve, "status":"SUCCEEDED"})
            outputs_dict["outputs"].append({"id":plot,"status": "SUCCEEDED","exception": None,"skipReason": None,"output": None,"duration": None,"curves": curves_list})
           

        for report in list(reports_dict.keys()):
            dataset_list = []
            #dataset_dict = {}
            for dataset in reports_dict[report]:
                dataset_list.append({"id":dataset , "status" :"QUEUED"})
            outputs_dict["outputs"].append({"id":report,"status": "QUEUED","exception": None,"skipReason": None,"output": None,"duration": None, "dataSets": dataset_list})
            #outputs_dict["outputs"][report].update({"status": "QUEUED"})

        for task in task_list:
            exception = {"category":None, "message":None}
            tasks_dict["tasks"].append({"id":task ,"status": "QUEUED", "exception": exception, "skipReason": None, "output": None, "duration": None, "algorithm": None,"simulatorDetails":None})

        sed_doc_dict = {"location":sedml,"status": "QUEUED", "exception": None,"skipReason": None,"output":None,"duration":None}
        sed_doc_dict.update(outputs_dict)
        sed_doc_dict.update(tasks_dict)
        yaml_dict.append(sed_doc_dict)
    final_dict = {}
    final_dict['sedDocuments'] = yaml_dict
    final_dict['status'] = "QUEUED"
    final_dict['exception'] = None
    final_dict['skipReason'] = None
    final_dict['duration'] = None
    final_dict['output'] = None


    status_yaml_path = os.path.join(out_dir, "log.yml")

    with open(status_yaml_path, 'w' , encoding="utf-8") as sy:
        sy.write(yaml.dump(final_dict))
    # return final_dict
    shutil.rmtree(tmp_dir)

def get_yaml_as_str(yaml_path: str):
    # Import yaml
    yaml_str = ''
    with open(yaml_path, 'r') as sy:
        yaml_str = sy.read()

    # Convert yaml to json
    yaml_dict = yaml.load(yaml_str, yaml.SafeLoader)
    return yaml_dict

def dump_yaml_dict(yaml_path: str, yaml_dict: str, out_dir: str):
    json_path = os.path.join(out_dir, "log.json")
    with open(yaml_path, 'w' , encoding="utf-8") as sy:
        sy.write(yaml.dump(yaml_dict))
        dump_json_dict(json_path,yaml_dict)

def dump_json_dict(json_path: str,yaml_dict: str):
    with open(json_path, 'w' , encoding="utf-8") as json_out:
        json.dump(yaml_dict,json_out,sort_keys=True,indent=4)


def update_status(sedml: str, task: str, status: str, out_dir: str ,duration: str, algorithm: str):
    # Hardcoded because name is static
    yaml_dict = get_yaml_as_str(os.path.join(out_dir, "log.yml"))
    for sedml_list in yaml_dict['sedDocuments']:
        if sedml.endswith(sedml_list["location"]):
            sedml_name_nested = sedml_list["location"]
            # Update task status
            for taskList in sedml_list['tasks']:
                if taskList['id'] == task:
                    taskList['status'] = status
                    taskList['duration'] = duration
                    taskList['algorithm'] = algorithm
                    # update individual SED-ML status
                    if taskList['status'] == 'QUEUED' or taskList['status']== 'SUCCEEDED':
                        sedml_list['status'] = 'SUCCEEDED'
                    else:
                        sedml_list['status'] = 'FAILED'

    status_yaml_path = os.path.join(out_dir, "log.yml")

    # Convert json to yaml # Save new yaml
    dump_yaml_dict(status_yaml_path, yaml_dict=yaml_dict, out_dir=out_dir)


def update_dataset_status(sedml: str, report: str, dataset: str, status: str, out_dir: str):
    yaml_dict = get_yaml_as_str(os.path.join(out_dir, "log.yml"))
    for sedml_list in yaml_dict['sedDocuments']:
        if sedml.endswith(sedml_list["location"]):
            sedml_name_nested = sedml_list["location"]
            # Update task status
            try:
                for outputList in sedml_list['outputs']:
                    if outputList['id'] == report:
                        for dataset_list in outputList['dataSets']:
                            if dataset_list['id'] == dataset:
                                dataset_list['status'] = status
                                if status == 'QUEUED' or status == 'SUCCEEDED':
                                    outputList['status']= 'SUCCEEDED'
                                else:
                                    outputList['status'] = 'FAILED'
            except KeyError:
                pass

    # update individual dataSets status
    '''
    for key in yaml_dict['sedDocuments'][sedml_name_nested]['outputs'].keys():
        try:
            for dataset_key in yaml_dict['sedDocuments'][sedml_name_nested]['outputs'][key]['dataSets'].keys():
                if yaml_dict['sedDocuments'][sedml_name_nested]['outputs'][key]['dataSets'][dataset_key] == 'QUEUED' or yaml_dict['sedDocuments'][sedml_name_nested]['outputs'][key]['dataSets'][dataset_key] == 'SUCCEEDED':
                    yaml_dict['sedDocuments'][sedml_name_nested]['outputs'][key]['status'] = 'SUCCEEDED'
                else:
                    yaml_dict['sedDocuments'][sedml_name_nested]['outputs'][key]['status'] = 'FAILED'
        except KeyError:
            continue
    '''

    status_yaml_path = os.path.join(out_dir, "log.yml")

    # Convert json to yaml # Save new yaml
    dump_yaml_dict(status_yaml_path, yaml_dict=yaml_dict, out_dir=out_dir)


def sim_status(status: str, out_dir: str):

    yaml_dict = get_yaml_as_str(os.path.join(out_dir, "log.yml"))

    # Update simulation status
    yaml_dict['status'] = status

    status_yaml_path = os.path.join(out_dir, "log.yml")

    # Convert json to yaml # Save new yaml
    dump_yaml_dict(status_yaml_path, yaml_dict=yaml_dict, out_dir=out_dir)

def set_output_message(sedml: str, task: str,out_dir:str, name:str , message: str):

    yaml_dict = get_yaml_as_str(os.path.join(out_dir, "log.yml"))
    for sedml_list in yaml_dict['sedDocuments']:
        if sedml.endswith(sedml_list["location"]):
            sedml_name_nested = sedml_list["location"]
            # Update task status
            if name == 'task':
                for taskList in sedml_list['tasks']:
                    if taskList['id'] == task:
                        taskList['output'] = message
    status_yaml_path = os.path.join(out_dir, "log.yml")

    # Convert json to yaml # Save new yaml
    dump_yaml_dict(status_yaml_path, yaml_dict=yaml_dict, out_dir=out_dir)

def set_exception_message(sedml: str, task: str,out_dir:str, name:str , category: str, message: str):

    yaml_dict = get_yaml_as_str(os.path.join(out_dir, "log.yml"))
    for sedml_list in yaml_dict['sedDocuments']:
        if sedml.endswith(sedml_list["location"]):
            sedml_name_nested = sedml_list["location"]
            # Update task status
            if name == 'task':
                for taskList in sedml_list['tasks']:
                    if taskList['id'] == task:
                        aaa = taskList['exception']
                        aaa['category'] = category
                        aaa['message'] = message
    status_yaml_path = os.path.join(out_dir, "log.yml")

    # Convert json to yaml # Save new yaml
    dump_yaml_dict(status_yaml_path, yaml_dict=yaml_dict, out_dir=out_dir)

if __name__ == "__main__":
    fire.Fire({
        'genStatusYaml': status_yml,
        'updateTaskStatus': update_status,
        'simStatus': sim_status,
        'updateDataSetStatus': update_dataset_status,
        'setOutputMessage' : set_output_message,
        'setExceptionMessage' : set_exception_message,
    })

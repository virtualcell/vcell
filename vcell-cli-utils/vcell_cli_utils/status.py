import os

import fire
from biosimulators_utils.archive.io import ArchiveReader
import libsedml
import yaml
import tempfile
import zipfile
import shutil
import json
from typing import  Dict, List

# Create temp directory
tmp_dir = tempfile.mkdtemp()


def extract_omex_archive(omex_file) -> list:
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


def status_yml(omex_file: str, out_dir: str) -> None:
    yaml_dicts: List[Dict] = []
    for sedml in extract_omex_archive(omex_file):
        outputs_dict: Dict[str, List[Dict]] = {"outputs": []}
        tasks_dict: Dict[str, List[Dict]] = {"tasks": []}
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
                curves_list.append({"id":curve, "status":"QUEUED"})
            outputs_dict["outputs"].append({"id":plot,"status": "QUEUED","exception": None,"skipReason": None,"output": None,"duration": None,"curves": curves_list})
           

        for report in list(reports_dict.keys()):
            dataset_list = []
            #dataset_dict = {}
            for dataset in reports_dict[report]:
                dataset_list.append({"id":dataset , "status" :"QUEUED"})
            exc_dict = {'type': "", 'message': ""}
            outputs_dict["outputs"].append({"id":report,"status": "QUEUED","exception": None,"skipReason": None,"output": None,"duration": None, "dataSets": dataset_list})
            #outputs_dict["outputs"][report].update({"status": "QUEUED"})

        for task in task_list:
            exc_dict = {'type': "", 'message': ""}
            tasks_dict["tasks"].append({"id": task ,"status": "QUEUED", "exception": None, "skipReason": None, "output": None, "duration": None, "algorithm": None, "simulatorDetails": None})

        exc_dict = {'type': "", 'message': ""}
        sed_doc_dict = {"location": sedml, "status": "QUEUED", "exception": None, "skipReason": None, "output": None, "duration": None}
        sed_doc_dict.update(outputs_dict)
        sed_doc_dict.update(tasks_dict)
        yaml_dicts.append(sed_doc_dict)
    exc_dict = {'type': "", 'message': ""}
    final_dict: Dict[str, List[Dict] | str | None] = {
        'sedDocuments': yaml_dicts,
        'status': "QUEUED",
        'exception': None,
        'skipReason': None,
        'duration': None,
        'output': None
    }

    status_yaml_path = os.path.join(out_dir, "log.yml")

    with open(status_yaml_path, 'w' , encoding="utf-8") as sy:
        sy.write(yaml.dump(final_dict))
    # "return" final_dict
    shutil.rmtree(tmp_dir)


def get_yaml_as_str(yaml_path: str) -> dict:
    # Import yaml
    yaml_str = ''
    with open(yaml_path, 'r') as sy:
        yaml_str = sy.read()

    # Convert yaml to json
    yaml_dict = yaml.load(yaml_str, yaml.SafeLoader)
    return yaml_dict


def dump_yaml_dict(yaml_path: str, yaml_dict: dict, out_dir: str):
    json_path = os.path.join(out_dir, "log.json")
    with open(yaml_path, 'w' , encoding="utf-8") as sy:
        sy.write(yaml.dump(yaml_dict))
        dump_json_dict(json_path,yaml_dict)


def dump_json_dict(json_path: str,yaml_dict: dict):
    with open(json_path, 'w' , encoding="utf-8") as json_out:
        json.dump(yaml_dict,json_out,sort_keys=True,indent=4)


def update_task_status(sedml: str, task: str, status: str, out_dir: str, duration: int, algorithm: str) -> None:
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
                    # if taskList['status'] == 'QUEUED' or taskList['status']== 'SUCCEEDED':
                    #     sedml_list['status'] = 'SUCCEEDED'
                    # else:
                    #     sedml_list['status'] = 'FAILED'

    status_yaml_path = os.path.join(out_dir, "log.yml")
    # Convert json to yaml # Save new yaml
    dump_yaml_dict(status_yaml_path, yaml_dict=yaml_dict, out_dir=out_dir)


def update_sedml_doc_status(sedml: str, status: str, out_dir: str) -> None:
    # Hardcoded because name is static
    yaml_dict = get_yaml_as_str(os.path.join(out_dir, "log.yml"))
    for sedml_list in yaml_dict['sedDocuments']:
        if sedml.endswith(sedml_list["location"]):
            sedml_name_nested = sedml_list["location"]
            sedml_list['status'] = status

    status_yaml_path = os.path.join(out_dir, "log.yml")
    dump_yaml_dict(status_yaml_path, yaml_dict=yaml_dict, out_dir=out_dir)


def update_omex_status(status: str, out_dir: str, duration: int) -> None:

    yaml_dict = get_yaml_as_str(os.path.join(out_dir, "log.yml"))
    yaml_dict['status'] = status
    yaml_dict['duration'] = duration

    status_yaml_path = os.path.join(out_dir, "log.yml")
    dump_yaml_dict(status_yaml_path, yaml_dict=yaml_dict, out_dir=out_dir)


def update_dataset_status(sedml: str, report: str, dataset: str, status: str, out_dir: str) -> None:
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
                                    outputList['status'] = 'SUCCEEDED'
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


def update_plot_status(sedml: str, plot_id: str, status: str, out_dir: str) -> None:
    yaml_dict = get_yaml_as_str(os.path.join(out_dir, "log.yml"))
    for sedml_list in yaml_dict['sedDocuments']:
        if sedml.endswith(sedml_list["location"]):
            sedml_name_nested = sedml_list["location"]
            # Update task status
            try:
                for output in sedml_list['outputs']:
                    if output['id'] == plot_id:
                        for curve in output['curves']:
                            curve['status'] = status
                            if status == 'QUEUED' or status == 'SUCCEEDED':
                                output['status'] = 'SUCCEEDED'
                            else:
                                output['status'] = 'FAILED'
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

#
# sedmlAbsolutePath - full path to location of the actual sedml file (document) used as input
# entityId          - (actually, the name) ex: task_0_0 for task, or biomodel_20754836.sedml for a sedml document
# outDir            - path to directory where the log files will be placed
# entityType        - string describing the entity type ex "task" for a task, or "sedml" for sedml document
#
def set_output_message(sedmlAbsolutePath: str, entityId: str, out_dir: str, entityType: str , message: str) -> None:

    yaml_dict = get_yaml_as_str(os.path.join(out_dir, "log.yml"))
    if entityType == 'omex':
        # update omex archive output message
        yaml_dict['output'] = message
    else:
        for sedml_list in yaml_dict['sedDocuments']:
            if sedmlAbsolutePath.endswith(sedml_list["location"]):
                sedml_name_nested = sedml_list["location"]
                # Update sedml document output message
                if entityType == 'sedml':
                    if sedml_name_nested == entityId:
                        sedml_list['output'] = message
            
                # Update task output message
                if entityType == 'task':
                    for taskList in sedml_list['tasks']:
                        if taskList['id'] == entityId:
                            taskList['output'] = message
                        
    status_yaml_path = os.path.join(out_dir, "log.yml")
    # Convert json to yaml # Save new yaml
    dump_yaml_dict(status_yaml_path, yaml_dict=yaml_dict, out_dir=out_dir)

def set_exception_message(sedmlAbsolutePath: str, entityId: str, out_dir: str, entityType: str, type: str, message: str) -> None:

    yaml_dict = get_yaml_as_str(os.path.join(out_dir, "log.yml"))
    for sedml_list in yaml_dict['sedDocuments']:
        if sedmlAbsolutePath.endswith(sedml_list["location"]):
            sedml_name_nested = sedml_list["location"]
            # Update sedml document status
            # print(" --- sedml: ", sedml_name_nested, file=sys.stdout)
            # print(" --- name: ", name, file=sys.stdout)
            if entityType == 'sedml':
                if sedml_name_nested == entityId:
                    exc_dict = {'type': type, 'message': message}
                    sedml_list['exception'] = exc_dict
                    #exc['type'] = type
                    #exc['message'] = message
            
            # Update task status
            if entityType == 'task':
                for taskList in sedml_list['tasks']:
                    if taskList['id'] == entityId:
                        exc_dict = {'type': type, 'message': message}
                        taskList['exception'] = exc_dict
                        #exc = taskList['exception']
                        #exc['type'] = type
                        #exc['message'] = message
    status_yaml_path = os.path.join(out_dir, "log.yml")

    # Convert json to yaml # Save new yaml
    dump_yaml_dict(status_yaml_path, yaml_dict=yaml_dict, out_dir=out_dir)


if __name__ == "__main__":
    fire.Fire({
        'genStatusYaml': status_yml,
        'updateTaskStatus': update_task_status,
        'updateSedmlDocStatus': update_sedml_doc_status,
        'updateOmexStatus': update_omex_status,
        'updateDataSetStatus': update_dataset_status,
        'updatePlotStatus': update_plot_status,
        'setOutputMessage': set_output_message,
        'setExceptionMessage': set_exception_message,
    })

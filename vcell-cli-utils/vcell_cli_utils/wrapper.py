__package__ = "vcell_cli_utils"
import sys
import glob
import os
from pathlib import Path
if __name__ == "__main__":
    from vcell_cli_utils import cli, status
else:
    from . import cli, status

# Since file is a wrapper access point for a java program, functions will use *java* name-style conventions (camel case)

def validateOmex(omexFilePath: str, tempDirPath: str, reportJsonFilePath: str) -> None:
    temp_path: Path = Path(tempDirPath) / "temp"
    files = glob.glob(str(temp_path) + "/*", recursive=False)
    for f in files:
        os.remove(f)

    cli.validate_omex(omex_file_path=omexFilePath, temp_dir_path=str(temp_path), omex_json_report_path=reportJsonFilePath)

    files = glob.glob(str(temp_path) + "/*", recursive=False)
    for f in files:
        os.remove(f)

    try:
        os.remove(temp_path)
    except:
        pass


def genSedml2d3d(omexFilePath : str, baseOutPath : str) -> None:
    cli.gen_sedml_2d_3d(omexFilePath, baseOutPath)

def genPlotsPseudoSedml(sedmlPath : str, resultOutDir : str) -> None:
    cli.gen_plots_for_sed2d_only(sedmlPath, resultOutDir)

# this function is no longer used
def execSedDoc(omexFilePath : str, baseOutPath : str) -> None:
    cli.exec_sed_doc(omexFilePath, baseOutPath)

def transposeVcmlCsv(csvFilePath: str) -> None:
    cli.transpose_vcml_csv(csvFilePath)

def genPlotPdfs(sedmlPath : str, resultOutDir : str) -> None:
    cli.gen_plot_pdfs(sedmlPath, resultOutDir)

# def genStatusYaml(omexFile: str, outDir: str) -> None:
#     status.status_yml(omexFile, outDir)

# def updateTaskStatus(sedml: str, task: str, statusVar: str, outDir: str, duration: str, algorithm: str) -> None:
#     status.update_task_status(sedml, task, statusVar, outDir, int(duration), algorithm)

# def updateSedmlDocStatus(sedml: str, statusVar: str, outDir: str) -> None:
#     status.update_sedml_doc_status(sedml, statusVar, outDir)

# def updateOmexStatus(statusVar: str, outDir: str, duration: str) -> None:
#     status.update_omex_status(statusVar, outDir, int(duration))

def updateDataSetStatus(sedml: str, report: str, dataset: str, statusVar: str, outDir: str) -> None:
    status.update_dataset_status(sedml, report, dataset, statusVar, outDir)

def updatePlotStatus(sedml: str, plot_id: str, statusVar: str, out_dir: str) -> None:
    status.update_plot_status(sedml, plot_id, statusVar, out_dir)

def setOutputMessage(sedmlAbsolutePath:str, entityId:str, outDir:str, entityType:str , message:str) -> None:
    status.set_output_message(sedmlAbsolutePath, entityId, outDir, entityType, message)

def setExceptionMessage(sedmlAbsolutePath:str, entityId:str, outDir:str, entityType:str, type:str, message:str) -> None:
    status.set_exception_message(sedmlAbsolutePath, entityId, outDir, entityType, type, message)

if __name__ == "__main__":
    args = sys.argv
    # args[0] = current file
    # args[1] = function name
    # args[2:] = function args : (*unpacked)
    globals()[args[1]](*args[2:])
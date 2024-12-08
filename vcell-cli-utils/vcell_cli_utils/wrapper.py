__package__ = "vcell_cli_utils"

import sys
import glob
import os
from pathlib import Path

if __name__ == "__main__":
    from vcell_cli_utils import cli
else:
    from . import cli


# Since file is a wrapper access point for a java program, functions will use *java* name-style conventions (camel case)

def validateOmex(omexFilePath: str, tempDirPath: str, reportJsonFilePath: str) -> None:
    temp_path: Path = Path(tempDirPath) / "temp"
    files = glob.glob(str(temp_path) + "/*", recursive=False)
    for f in files:
        os.remove(f)

    cli.validate_omex(omex_file_path=omexFilePath, temp_dir_path=str(temp_path),
                      omex_json_report_path=reportJsonFilePath)

    files = glob.glob(str(temp_path) + "/*", recursive=False)
    for f in files:
        os.remove(f)

    try:
        os.remove(temp_path)
    except:
        pass


def genSedml2d3d(omexFilePath: str, baseOutPath: str) -> None:
    cli.gen_sedml_2d_3d(omexFilePath, baseOutPath)


def genPlotsPseudoSedml(sedmlPath: str, resultOutDir: str) -> None:
    cli.gen_plots_for_sed2d_only(sedmlPath, resultOutDir)


def transposeVcmlCsv(csvFilePath: str) -> None:
    cli.transpose_vcml_csv(csvFilePath)


def genPlotPdfs(sedmlPath: str, resultOutDir: str) -> None:
    cli.gen_plot_pdfs(sedmlPath, resultOutDir)


if __name__ == "__main__":
    args = sys.argv
    # args[0] = current file
    # args[1] = function name
    # args[2:] = function args : (*unpacked)
    globals()[args[1]](*args[2:])

from pyvcell_fvsolver import __version__, version, solve
from pathlib import Path
import os

# get parent directory of this script as a path
parent_dir: Path = Path(os.path.dirname(os.path.realpath(__file__))).parent
test_data_dir = parent_dir / "test_data"
fv_input_file = test_data_dir / "SimID_946368938_0_.fvinput"
vcg_input_file = test_data_dir / "SimID_946368938_0_.vcg"
test_output_dir_1 = parent_dir / "test_output_1"
test_output_dir_2 = parent_dir / "test_output_2"

def test_version_var():
    assert __version__ == "0.0.1"

def test_version_func():
    assert version() is not None

def test_solve():
    # empty directory test_output_dir_1 and test_output_dir_2
    for file in test_output_dir_1.iterdir() if test_output_dir_1.exists() else []:
        file.unlink()
    for file in test_output_dir_2.iterdir() if test_output_dir_2.exists() else []:
        file.unlink()

    retcode_1: int = solve(fvInputFilename=str(fv_input_file), vcgInputFilename=str(vcg_input_file), outputDir=str(test_output_dir_1))
    print(f"retcode_1: {retcode_1}")

    # retcode_2: int = solve(fvInputFilename=str(fv_input_file), vcgInputFilename=str(vcg_input_file), outputDir=str(test_output_dir_2))
    # print(f"retcode_2: {retcode_2}")
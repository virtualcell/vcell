import filecmp
import os
from pathlib import Path

import pytest

from vcell_cli_utils import wrapper

testdata = [
    ("Aavani2019_BIOMD0000000876_vcell_7.5.0.27.omex",
     "Aavani2019_BIOMD0000000876_vcell_7.5.0.27.report.json"),
    ("VerySimple_vcell_7.5.0.27.omex",
     "VerySimple_vcell_7.5.0.27.report.json"),
    ("eungdamrong_and_Iyengar_BiophysJ_vcell_7.5.0.30.omex",
     "eungdamrong_and_Iyengar_BiophysJ_vcell_7.5.0.30.report.json")
]


@pytest.mark.parametrize("input_omex,expected_report", testdata)
def test_wrapper_validate_omex(input_omex: str, expected_report: str) -> None:
    temp_path: Path = Path(__file__).parent.parent
    omex_file: Path = Path(__file__).parent / "fixtures" / "validation" / input_omex
    expected_report_file: Path = Path(__file__).parent / "fixtures" / "validation" / expected_report
    generated_report_file: Path = Path(__file__).parent / "fixtures" / "problem.report"

    if generated_report_file.exists():
        os.remove(generated_report_file)

    wrapper.validateOmex(str(omex_file), str(temp_path), str(generated_report_file))

    filecmp.clear_cache()
    bSame: bool = filecmp.cmp(expected_report_file, generated_report_file, shallow=False)


    assert bSame

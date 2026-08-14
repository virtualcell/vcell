import json
import os
import stat
import tempfile
from typing import List

from biosimulators_utils.combine.data_model import CombineArchive, CombineArchiveContentFormat
from biosimulators_utils.combine.io import CombineArchiveReader
from biosimulators_utils.combine.validation import validate
from biosimulators_utils.config import Config

# Move status PY code here
# Create temp directory
tmp_dir = tempfile.mkdtemp()

def validate_omex(omex_file_path: str, temp_dir_path: str, omex_json_report_path: str) -> str:
    if not os.path.exists(temp_dir_path):
        os.mkdir(temp_dir_path, stat.S_IRWXU | stat.S_IRWXG | stat.S_IRWXO)

    # defining archive
    config = Config(
        VALIDATE_OMEX_MANIFESTS=True,
        VALIDATE_SEDML=True,
        VALIDATE_SEDML_MODELS=True,
        VALIDATE_IMPORTED_MODEL_FILES=True,
        VALIDATE_OMEX_METADATA=True,
        VALIDATE_IMAGES=True,
        VALIDATE_RESULTS=True
    )

    reader = CombineArchiveReader()
    archive: CombineArchive = reader.run(in_file=omex_file_path, out_dir=temp_dir_path, config=config)
    print("errors: "+str(reader.errors)+"\n"+"warnings: "+str(reader.warnings))

    validator_errors: List[str] = []
    validator_warnings: List[str] = []
    if len(reader.errors) == 0:
        validator_errors, validator_warnings = validate(
            archive,
            temp_dir_path,
            formats_to_validate=list(CombineArchiveContentFormat.__members__.values()),
            config=config
        )

    results_dict = {
                "parse_errors": reader.errors,
                "parse_warnings": reader.warnings,
                "validator_errors": validator_errors,
                "validator_warnings": validator_warnings
            }
    with open(omex_json_report_path, "w") as file:
        file.write(json.dumps(results_dict, indent=2))
    return repr(results_dict)

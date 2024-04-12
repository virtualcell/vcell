import os
import re
from pathlib import Path
from typing import Optional

import requests
from pydantic import BaseModel

from vcutils.common.api_utils import download_file
from vcutils.vcell_pipeline.citation import getCitation, CitationInfo, getSuggestedProjectName
from vcutils.vcell_pipeline.datamodels import Publication

def prune_already_published_models(target_dir: Path):
    response = requests.get("https://api.biosimulations.org/projects", headers={'accept': 'application/json'})
    publications: list[dict] = response.json()
    ids = [publication["id"] for publication in publications]
    vcdb_pubs: set[str] = set([elem for elem in ids if "VCDB" in elem])
    vcdb_ids = set([re.split("[\-_]+", parts)[1] for parts in vcdb_pubs])
    files = {re.split("[\-_]+", file)[1] : file for file in os.listdir(target_dir)}

    for potential_id in vcdb_ids:
        if potential_id in files:
            os.remove(os.path.join(target_dir, files[potential_id]))
            print(f"Removed file: {files[potential_id]}")


if __name__ == "__main__":
    prune_already_published_models("/home/ldrescher/Development/BioSim/publication_candidates/input")

import os
from pathlib import Path
from typing import Optional
from download_vcell_omex import ExportStatus

import requests
from pydantic import BaseModel

from vcutils.common.api_utils import download_file
from vcutils.vcell_pipeline.citation import getCitation, CitationInfo, getSuggestedProjectName
from vcutils.vcell_pipeline.datamodels import Publication

VERIFY_SSL = False


def rename_published_omex(api_base_url: str, subject_dir: Path) -> None:
    response = requests.get(f"{api_base_url}/publication", headers={'Accept': 'application/json'}, verify=VERIFY_SSL)
    publication_json = response.json()
    id_to_name_mapping: dict[str, str] = {}
    files_to_check = os.listdir(subject_dir)
    starting_count = len(files_to_check)
    changes_count = 0

    for pub in [Publication(**jsonDict) for jsonDict in publication_json]:
        if len(pub.biomodelReferences) == 0:
            continue
        print(f"Processing {pub.pubKey}, title: {pub.title}, year: {pub.year}, bimodels: {pub.biomodelReferences}")
        bmKey = pub.biomodelReferences[0].bmKey
        pubmedId: Optional[str] = pub.pubmedid
        citationInfo: Optional[CitationInfo] = None
        try:
            citationInfo = getCitation(pubmedId)
        except Exception as e:
            print(f"Error getting citation for {pubmedId}: {e}")

        for bioModelKey in [bmr.bmKey for bmr in pub.biomodelReferences]:
            suggested_project_name = getSuggestedProjectName(bm_key=bmKey, pub_info=pub, citation_info=citationInfo)
            id_to_name_mapping[bioModelKey] = suggested_project_name

    for file in files_to_check:
        file_key = file[9:-5]
        if not file_key.isdigit():
            continue
        if file_key in id_to_name_mapping:
            changes_count += 1
            os.rename(os.path.join(subject_dir, file), os.path.join(subject_dir, id_to_name_mapping[file_key]))
        else:
            print(" > No corresponding file to rename with key: {}".format(file_key))

    ending_count = len(os.listdir(subject_dir))
    print(f"\t> Starting files: {starting_count}\n\t> Changes Made: {changes_count}\n\t> Finished files: {ending_count}")




if __name__ == "__main__":
    rename_published_omex(
        api_base_url="https://vcellapi-beta.cam.uchc.edu:8080",
        subject_dir=Path("/home/ldrescher/Documents/convertedFiles")
    )

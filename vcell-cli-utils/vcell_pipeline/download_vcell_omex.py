from pathlib import Path

import requests
from pydantic import BaseModel

from citation import getCitation, CitationInfo, getSuggestedProjectName
from vcell_common.api_utils import download_file
from vcell_pipeline.vcell_datamodels import Publication


class ExportStatus(BaseModel):
    bmKey: str
    wrote_vcml: bool = False
    wrote_sbml: bool = False
    wrote_omex: bool = False
    exception: str | None = None


def write_log(entry: ExportStatus, log_path: Path) -> None:
    with open(log_path, "a") as f:
        f.write(entry.json()+"\n")
        f.flush()


def download_published_omex(api_base_url: str, out_dir: Path) -> None:
    log_path = Path(out_dir, "export.log")

    response = requests.get(f"{api_base_url}/publication", headers={'Accept': 'application/json'}, verify=False)
    publication_json = response.json()
    pubs = [Publication(**jsonDict) for jsonDict in publication_json]

    for pub in pubs:
        if len(pub.biomodelReferences) == 0:
            continue

        bmKey = pub.biomodelReferences[0].bmKey
        pubmedId: str | None = pub.pubmedid
        citationInfo: CitationInfo | None = None
        try:
            citationInfo = getCitation(pubmedId)
        except Exception as e:
            print(f"Error getting citation for {pubmedId}: {e}")

        suggestedProjectName = getSuggestedProjectName(bm_key=bmKey, pub_info=pub, citation_info=citationInfo)
        exportStatus = ExportStatus(bmKey=bmKey)

        try:
            vcml_url = f"{api_base_url}/biomodel/{bmKey}/biomodel.vcml"
            vcml_file = Path(out_dir, f"{suggestedProjectName}.vcml")
            download_file(url=vcml_url, out_file=vcml_file)
            exportStatus.wrote_vcml = True

            sbml_url = f"{api_base_url}/biomodel/{bmKey}/biomodel.sbml"
            sbml_file = Path(out_dir, f"{suggestedProjectName}.sbml")
            download_file(url=sbml_url, out_file=sbml_file)
            exportStatus.wrote_sbml = True

            omex_url = f"{api_base_url}/biomodel/{bmKey}/biomodel.omex"
            omex_file = Path(out_dir, f"{suggestedProjectName}.omex")
            download_file(url=omex_url, out_file=omex_file)
            exportStatus.wrote_omex = True

        except requests.exceptions.HTTPError as e:
            error_msg = str(e)
            error_response: requests.Response = e.response
            if error_response.status_code == 500:
                error_msg += " "+error_response.text
            print(error_msg)
            exportStatus.exception = error_msg

        write_log(exportStatus, log_path)


if __name__ == "__main__":
    download_published_omex(
        # api_base_url="https://vcellapi-beta.cam.uchc.edu:8080",
        api_base_url="https://localhost:8083",
        out_dir=Path("/Users/schaff/Documents/workspace/vcell/export4")
    )
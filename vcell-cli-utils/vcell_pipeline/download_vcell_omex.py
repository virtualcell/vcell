from pathlib import Path

import requests
from pydantic import BaseModel

from vcell_datamodels import Publication


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


def download_file(url: str, out_file: Path) -> None:
    """
    download file using streaming to support large files

    :param str url: the url to download from
    :param Path out_file: the file to write the downloaded contents to
    :raises requests.exceptions.HTTPError: if the download fails
    """
    with requests.Session() as session:
        response = session.get(url, verify=False, stream=True)
        response.raise_for_status()
        assert response.status_code == 200  # raise_for_status should throw if not 200
        with open(out_file, "wb") as f:
            for chunk in response.iter_content(chunk_size=10000):
                if chunk:  # filter out keep-alive new chunks
                    f.write(chunk)


def download_published_omex(api_base_url: str, out_dir: Path) -> None:
    log_path = Path(out_dir, "export.log")

    response = requests.get(f"{api_base_url}/publication", headers={'Accept': 'application/json'}, verify=False)
    publication_json = response.json()
    pubs = [Publication(**jsonDict) for jsonDict in publication_json]

    for pub in pubs:
        if len(pub.biomodelReferences) == 0:
            continue

        bmKey = pub.biomodelReferences[0].bmKey
        exportStatus = ExportStatus(bmKey=bmKey)

        try:
            vcml_url = f"{api_base_url}/biomodel/{bmKey}/biomodel.vcml"
            vcml_file = Path(out_dir, f"biomodel_{bmKey}.vcml")
            download_file(vcml_url, vcml_file)
            exportStatus.wrote_vcml = True

            sbml_url = f"{api_base_url}/biomodel/{bmKey}/biomodel.sbml"
            sbml_file = Path(out_dir, f"biomodel_{bmKey}.sbml")
            download_file(sbml_url, sbml_file)
            exportStatus.wrote_sbml = True

            omex_url = f"{api_base_url}/biomodel/{bmKey}/biomodel.omex"
            omex_file = Path(out_dir, f"biomodel_{bmKey}.omex")
            download_file(omex_url, omex_file)
            exportStatus.wrote_omex = True

        except requests.exceptions.HTTPError as e:
            print(str(e))
            exportStatus.exception = str(e)

        write_log(exportStatus, log_path)


if __name__ == "__main__":
    download_published_omex(
        api_base_url="https://localhost:8083",
        out_dir=Path("/Users/schaff/Documents/workspace/vcell/export")
    )

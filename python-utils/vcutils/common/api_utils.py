from pathlib import Path

import requests


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

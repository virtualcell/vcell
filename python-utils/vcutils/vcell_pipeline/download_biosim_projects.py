import os
from zipfile import ZipFile

import requests


def download_reference_projects():
    api_base_url = os.environ.get('API_BASE_URL')
    num_models = 10
    project_query = f'{api_base_url}/projects/summary_filtered?' \
                    f'pageSize={num_models}' \
                    '&pageIndex=0' \
                    '&searchText=' \
                    '&filters=[' \
                    '{"target":"keywords","allowable_values":["Biomodels"]},' \
                    '{"target":"simulator","allowable_values":["tellurium 2.2.8"]}' \
                    ']'

    projects = requests.get(project_query).json()

    for projectSummary in projects['projectSummaries']:
        project_id = projectSummary['id']
        run = projectSummary['simulationRun']['id']
        r = requests.get(f'{api_base_url}/runs/{run}/download', allow_redirects=True)
        with open(f'{project_id}.spec.omex', 'wb') as f:
            f.write(r.content)
        r = requests.get(f'{api_base_url}/logs/{run}', allow_redirects=True)
        with open(f'{project_id}.logs.json', 'wb') as f:
            f.write(r.content)
        r = requests.get(f'{api_base_url}/results/{run}?includeData=true', allow_redirects=True)
        with open(f'{project_id}.outputs.json', 'wb') as f:
            f.write(r.content)
        r = requests.get(f'{api_base_url}/results/{run}/download')
        with open(f'{project_id}.results.zip', 'wb') as f:
            f.write(r.content)
        with ZipFile(f'{project_id}.results.zip', 'r') as zip_ref:
            for zipInfo in zip_ref.filelist:
                if zipInfo.filename.endswith(".h5"):
                    with open(f'{project_id}.h5', 'wb') as f:
                        f.write(zip_ref.read(zipInfo))
                if zipInfo.filename.endswith(".pdf"):
                    with open(f'{project_id}.pdf', 'wb') as f:
                        f.write(zip_ref.read(zipInfo))
        os.remove(f'{project_id}.results.zip')


if __name__ == "__main__":
    download_reference_projects()

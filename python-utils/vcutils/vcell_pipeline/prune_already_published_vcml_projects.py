import os
import re
from typing import Union

import typer
from dotenv import load_dotenv
from typer.core import TyperGroup

import vcutils.biosim_pipeline.biosim_api as bsi

class NaturalOrderGroup(TyperGroup):
    def list_commands(self, ctx):
        return self.commands.keys()


app = typer.Typer(cls=NaturalOrderGroup)

def main(*args, **kwargs):
    app()
    """
    if len(args) != 1:
        print("Please provide the path to the directory of omexes to prune\n\targuments provided:")
        for arg in args:
            print(f"\t{arg}")
        return
    dir_path = args[0]
    if not os.path.isdir(dir_path):
        print(f"`{dir_path}` is not a valid directory; please check your path.")
    """
    dir_path = "/home/ldrescher/Development/other/vcdb_upload_test"
    delete_already_published_omexes_from_directory(dir_path)


@app.command("prune_")
def delete_already_published_omexes_from_directory(file_path: str) -> None:
    already_published_vcdbs: set[str] = {re.split('[_-]', entry)[1] for entry in get_already_published_ids()}
    potential_new_projects: list[str] = get_projects_ids_from_folder(file_path)
    potential_new_projects_set: set[str] = set(potential_new_projects)
    for already_published in already_published_vcdbs:
        if already_published in potential_new_projects:
            potential_new_projects_set.remove(already_published)
    retain_projects_in_folder(file_path, potential_new_projects_set)


def get_already_published_ids() -> list[str]:
    pids: list[str] = []
    projects: list[dict] = bsi.get_projects()
    for project in projects:
        pid: str = project["id"]
        if not pid.startswith("VCDB"):
            continue
        pids.append(pid)
    return pids


def get_projects_ids_from_folder(file_path: str) -> list[str]:
    omex_files: list[str] = [f for f in os.listdir(file_path) if f.endswith(".omex")]
    ids: list[str] = [file_name.split("_")[1] for file_name in omex_files]
    return ids


def retain_projects_in_folder(file_path: str, vcdb_ids: Union[list[str], set[str]]):
    file: str
    for file in {f for f in os.listdir(file_path) if f.endswith(".omex")}:
        file_id: str = re.split('[_-]', file)[1]
        if file_id not in vcdb_ids:
            os.remove(os.path.join(file_path, file))


if __name__ == "__main__":
    main()

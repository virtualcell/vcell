from typing import Annotated, Union
import os

import typer
import urllib
import urllib.request
from dotenv import load_dotenv
from pathlib import Path

from biosim_api import run_project, check_run_status, publish_project
from data_manager import DataManager
from datamodels import Simulator, SimulationRun, SimulatorComparison
from vcutils.vcell_common.api_utils import download_file
from hdf5_compare import compare_datasets, get_results


app = typer.Typer()


@app.command("upload_omex", help="upload and run OMEX files at BioSimulations")
def upload_omex(
        simulator: Annotated[Simulator, typer.Option(help="simulator to run")] = Simulator.vcell,
        project_id: Annotated[Union[str, None], typer.Option(help="filter by project_id")] = None,
        omex_src_dir: Annotated[Union[Path, None], typer.Option(help="defaults env.OMEX_SOURCE_DIR")] = None,
        out_dir: Annotated[Union[Path, None], typer.Option(help="defaults to env.OMEX_OUTPUT_DIR")] = None
) -> None:

    load_dotenv()
    data_manager = DataManager(omex_src_dir=omex_src_dir, out_dir=out_dir)

    projects = data_manager.read_projects()

    for source_omex in data_manager.get_source_omex_archives():
        if source_omex.project_id in projects:
            print(f"project {source_omex.project_id} is already validated and published")
            continue
        if project_id is not None and source_omex.project_id != project_id:
            continue
        print(source_omex.project_id)
        run_project(source_omex=source_omex, simulator=simulator, data_manager=data_manager)


@app.command("refresh_status", help="fetch status of runs and update biosimulations_runs.ndjson")
def refresh_status(
        out_dir: Annotated[Union[Path, None], typer.Option(help="defaults to env.OMEX_OUTPUT_DIR")] = None,
) -> None:
    load_dotenv()
    data_manager = DataManager(out_dir=out_dir)

    runs: list[SimulationRun] = data_manager.read_run_requests()
    for run in runs:
        if run.status.lower() == "succeeded" or run.status.lower() == "failed":
            continue
        run.status = check_run_status(run)
    data_manager.write_runs(runs)



@app.command("download_runs", help="download runs (results.zip) from BioSimulations")
def download_runs(
        omex_src_dir: Annotated[Union[Path, None], typer.Option(help="defaults env.OMEX_SOURCE_DIR")] = None,
        out_dir: Annotated[Union[Path, None], typer.Option(help="defaults to env.OMEX_OUTPUT_DIR")] = None,
        project_id: Annotated[Union[str, None], typer.Option(help="filter by project_id")] = None,
        simulator: Annotated[Union[Simulator, None], typer.Option(help="filter by simulator")] = None,
) -> None:
    load_dotenv()
    data_manager = DataManager(omex_src_dir=omex_src_dir, out_dir=out_dir)

    runs: list[SimulationRun] = data_manager.read_run_requests()

    for run in runs:
        # filter by project_id and simulator
        if project_id and project_id != run.project_id:
            continue
        if simulator and simulator != run.simulator:
            continue
        if run.status.lower() != "succeeded":
            continue

        print("Retrieving", run.model_dump_json())

        simdir = data_manager.get_run_output_dir(run)
        if os.path.exists(simdir / "results.zip"):
            continue

        try:
            download_file(url="https://api.biosimulations.org/results/" + run.simulation_id + "/download",
                          out_file=Path(simdir / "results.zip"))
        except urllib.error.HTTPError as e:
            print("Failure:", e)


@app.command("compare_runs", help="compare downloaded runs")
def compare_runs(
        omex_src_dir: Annotated[Union[Path, None], typer.Option(help="defaults env.OMEX_SOURCE_DIR")] = None,
        out_dir: Annotated[Union[Path, None], typer.Option(help="defaults to env.OMEX_OUTPUT_DIR")] = None,
        project_id: Annotated[Union[str, None], typer.Option(help="filter by project_id")] = None,
) -> None:
    load_dotenv()
    data_manager = DataManager(omex_src_dir=omex_src_dir, out_dir=out_dir)

    runs: list[SimulationRun] = data_manager.read_run_requests()

    # get unique list of project_ids from runs
    unique_project_ids: list[str] = [*sorted(set([run.project_id for run in runs]))]
    for proj_id in unique_project_ids:
        if project_id is not None and proj_id != project_id:
            continue
        project_runs = [run for run in runs if run.project_id == proj_id]
        if len(project_runs) < 2:
            print(f"project {proj_id}, didn't have results from two simulators: << Can't Compare >>")
            continue

        # loop through each unique pair of runs in project_runs
        for i in range(len(project_runs)):
            run1: SimulationRun = project_runs[i]
            if run1.status.lower() != "succeeded":
                continue
            zip1 = data_manager.get_run_output_dir(run1) / "results.zip"
            if not os.path.exists(zip1):
                print("need to download results.zip for", run1.model_dump_json())
                continue
            results1 = get_results(zip1)

            for j in range(i+1, len(project_runs)):
                run2: SimulationRun = project_runs[j]
                if run2.status.lower() != "succeeded":
                    continue
                zip2 = data_manager.get_run_output_dir(run2) / "results.zip"
                if not os.path.exists(zip2):
                    print("need to download results.zip for", run2.model_dump_json())
                    continue
                results2 = get_results(zip2)

                equivalent = compare_datasets(results1, results2)
                comp_12 = SimulatorComparison.model_construct(project_id=proj_id, simRun1=run1, simRun2=run2, equivalent=equivalent)
                comp_21 = SimulatorComparison.model_construct(project_id=proj_id, simRun1=run2, simRun2=run1, equivalent=equivalent)
                if any(p.model_dump_json() in (comp_12.model_dump_json(), comp_21.model_dump_json())
                       for p in data_manager.read_comparisons()):
                    continue
                data_manager.write_comparison(comp_12)
                print(f"project {proj_id}, comparing {run1.simulator}:{run1.simulator_version} <=> {run2.simulator}:{run2.simulator_version}, equivalent:", equivalent)


def _pick_one(project_id: str, validated: list[SimulationRun]) -> SimulationRun:
    if len(validated) == 0:
        raise ValueError(f"no validated runs for {project_id}")
    if len(validated) == 1:
        return validated[0]
    sim_index = hash(project_id) % len(validated)
    return validated[sim_index]


@app.command("publish", help="publish validated projects to BioSimulations")
def publish(
        omex_src_dir: Annotated[Union[Path, None], typer.Option(help="defaults env.OMEX_SOURCE_DIR")] = None,
        out_dir: Annotated[Union[Path, None], typer.Option(help="defaults to env.OMEX_OUTPUT_DIR")] = None,
        project_id: Annotated[Union[str, None], typer.Option(help="filter by project_id")] = None,
) -> None:
    load_dotenv()
    data_manager = DataManager(omex_src_dir=omex_src_dir, out_dir=out_dir)

    runs: list[SimulationRun] = data_manager.read_run_requests()
    unique_project_ids = [*sorted(set([run.project_id for run in runs]))]
    published_projects = data_manager.read_projects()
    comparisons: list[SimulatorComparison] = data_manager.read_comparisons()
    for pid in unique_project_ids:
        if project_id is not None and pid != project_id:
            continue

        if any(p.project_id == pid for p in published_projects):
            print(pid, "already published")
            return

        validSims: list[SimulationRun] = []
        for comparison in comparisons:
            if comparison.equivalent and comparison.project_id == pid:
                validSims.append(comparison.simRun1)
                validSims.append(comparison.simRun2)
        if len(validSims) == 0:
            print(pid, "no valid runs")
            continue
        simRunToPublish = _pick_one(project_id=pid, validated=validSims)
        publish_project(data_manager=data_manager, run=simRunToPublish, overwrite=False)


if __name__ == "__main__":
    app()

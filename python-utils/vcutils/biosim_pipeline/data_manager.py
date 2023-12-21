import json
import os
from pathlib import Path
from typing import Optional

from vcutils.biosim_pipeline.datamodels import (
    BiosimulationsProject,
    SourceOmex,
    SimulationRun,
    SimulatorComparison
)


def _get_project_name(omex_file: Path) -> str:
    return str(omex_file.name).split(".")[0]


class DataManager(object):
    omex_src_dir: Path
    out_dir: Path
    projects_ndjson_file: Path
    runs_ndjson_file: Path

    def __init__(self, omex_src_dir: Optional[Path] = None, out_dir: Optional[Path] = None):
        self.omex_src_dir = Path(os.environ.get("OMEX_SOURCE_DIR", "OMEX_SOURCE_DIR-not-specified"))
        if omex_src_dir is not None:
            self.omex_src_dir = omex_src_dir
        if not os.path.exists(self.omex_src_dir):
            raise ValueError(f"Base source directory {self.omex_src_dir} does not exist")

        self.out_dir = Path(os.environ.get("OMEX_OUTPUT_DIR", "OMEX_OUTPUT_DIR-not-specified"))
        if out_dir is not None:
            self.out_dir = out_dir
        if not os.path.exists(self.out_dir):
            os.makedirs(self.out_dir)

        self.projects_ndjson_file = self.out_dir / 'biosimulations_projects.ndjson'
        self.runs_ndjson_file = self.out_dir / 'biosimulations_runs.ndjson'
        self.compare_ndjson_file = self.out_dir / 'biosimulations_comparisons.ndjson'

    def read_run_requests(self) -> list[SimulationRun]:
        projects: list[SimulationRun]
        if os.path.exists(self.runs_ndjson_file):
            with open(self.runs_ndjson_file) as f:
                projects = [SimulationRun(**json.loads(line)) for line in f.readlines()]
        else:
            projects = []
        return projects

    def read_projects(self) -> list[BiosimulationsProject]:
        projects: list[BiosimulationsProject]
        if os.path.exists(self.projects_ndjson_file):
            with open(self.projects_ndjson_file) as f:
                projects = [BiosimulationsProject(**json.loads(line)) for line in f.readlines()]
        else:
            projects = []
        return projects

    def write_project(self, project: BiosimulationsProject) -> None:
        with open(self.projects_ndjson_file, 'a') as f:
            f.write(json.dumps(project.dict()) + "\n")

    def get_spec_omex_list(self) -> list[Path]:
        omex_files: list[Path] = []
        for omex_file in os.listdir(self.omex_src_dir):
            if not str(omex_file).endswith(".omex"):
                continue
            omex_files.append(self.omex_src_dir / str(omex_file))
        return omex_files

    def get_source_omex_archives(self) -> list[SourceOmex]:
        source_omex_archives: list[SourceOmex] = []
        for omex_file_name in os.listdir(self.omex_src_dir):
            if not str(omex_file_name).endswith(".omex"):
                continue
            omex_file = self.omex_src_dir / str(omex_file_name)
            project_id = _get_project_name(omex_file)
            source_omex_archives.append(SourceOmex(omex_file_path=omex_file, project_id=project_id))
        return source_omex_archives

    def write_run(self, simulation_run: SimulationRun) -> None:
        with open(self.runs_ndjson_file, 'a') as f:
            f.write(json.dumps(simulation_run.dict()) + "\n")

    def write_runs(self, runs: list[SimulationRun]):
        with open(self.runs_ndjson_file, 'wt') as f:
            for run in runs:
                f.write(json.dumps(run.dict()) + "\n")

    def write_comparison(self, simulation_comparison: SimulatorComparison) -> None:
        with open(self.compare_ndjson_file, 'a') as f:
            f.write(json.dumps(simulation_comparison.dict()) + "\n")

    def read_comparisons(self) -> list[SimulatorComparison]:
        comparisons: list[SimulatorComparison]
        if os.path.exists(self.compare_ndjson_file):
            with open(self.compare_ndjson_file) as f:
                comparisons = [SimulatorComparison(**json.loads(line)) for line in f.readlines()]
        else:
            comparisons = []
        return comparisons

    def get_run_output_dir(self, simulation_run: SimulationRun) -> Path:
        run_out_dir = self.out_dir / simulation_run.project_id / simulation_run.simulator.value / simulation_run.simulator_version
        if not os.path.exists(run_out_dir):
            os.makedirs(run_out_dir)
        return run_out_dir

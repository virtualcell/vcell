from enum import Enum
from pathlib import Path
from typing import Optional

from pydantic import BaseModel


class BiosimulationsProject(BaseModel):
    project_id: str
    simulation_id: str


class Simulator(str, Enum):
    tellurium: str = "tellurium",
    copasi: str = "copasi",
    amici: str = "amici",
    vcell: str = "vcell",
    pysces: str = "pysces",
    libsbmlsim: str = "libsbmlsim"


class SimulationRun(BaseModel):
    simulator: Simulator
    simulator_version: str
    simulation_id: str
    project_id: str
    status: Optional[str] = "Unknown"


class SourceOmex(BaseModel):
    project_id: str
    omex_file_path: Path


class SimulatorComparison(BaseModel):
    project_id: str
    simRun1: SimulationRun
    simRun2: SimulationRun
    equivalent: bool



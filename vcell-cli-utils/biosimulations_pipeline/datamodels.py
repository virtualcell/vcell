from enum import Enum
from pathlib import Path
from typing import Optional

from pydantic import BaseModel


class BiosimulationsProject(BaseModel):
    project_id: str
    simulation_id: str


class Simulator(str, Enum):
    tellurium = "tellurium",
    copasi = "copasi",
    amici = "amici",
    vcell = "vcell",
    pysces = "pysces",
    libsbmlsim = "libsbmlsim"


class SimulationRun(BaseModel):
    simulator: Simulator
    simulator_version: str
    simulation_id: str
    project_id: str
    status: Optional[str] = "Unknown"


class SourceOmex(BaseModel):
    project_id: str
    omex_file: Path


class SimulatorComparison(BaseModel):
    project_id: str
    simRun1: SimulationRun
    simRun2: SimulationRun
    equivalent: bool



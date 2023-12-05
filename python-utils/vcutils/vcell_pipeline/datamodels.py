from pydantic import BaseModel
from typing import Optional


class BiomodelReference(BaseModel):
    bmKey: str
    name: str
    ownerName: str
    ownerKey: str
    versionFlag: str


class MathmodelReference(BaseModel):
    mmKey: str
    name: str
    ownerName: str
    ownerKey: str
    versionFlag: str


class Publication(BaseModel):
    pubKey: str
    title: str
    authors: list[str]
    year: int
    citation: Optional[str]
    pubmedid: Optional[str]
    doi: Optional[str]
    endnoteid: Optional[str]
    url: Optional[str]
    wittid: Optional[str]
    biomodelReferences: list[BiomodelReference]
    mathmodelReferences: list[MathmodelReference]
    date: str

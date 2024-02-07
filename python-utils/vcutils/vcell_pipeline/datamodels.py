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
    citation: Optional[str] = None
    pubmedid: Optional[str] = None
    doi: Optional[str] = None
    endnoteid: Optional[str] = None
    url: Optional[str] = None
    wittid: Optional[str] = None
    biomodelReferences: list[BiomodelReference]
    mathmodelReferences: list[MathmodelReference]
    date: str

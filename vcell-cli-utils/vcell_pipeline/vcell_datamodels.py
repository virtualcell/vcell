from pydantic import BaseModel


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
    citation: str | None
    pubmedid: str | None
    doi: str | None
    endnoteid: str | None
    url: str | None
    wittid: str | None
    biomodelReferences: list[BiomodelReference]
    mathmodelReferences: list[MathmodelReference]
    date: str

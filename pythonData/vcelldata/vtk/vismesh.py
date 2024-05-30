from dataclasses import dataclass
from typing import List, Optional


@dataclass
class Vect3D:
    x: float
    y: float
    z: float


@dataclass
class VisPoint:
    x: float
    y: float
    z: float


@dataclass
class ChomboVolumeIndex:
    level: int
    boxNumber: int
    boxIndex: int
    fraction: float


@dataclass
class ChomboSurfaceIndex:
    index: int


@dataclass
class MovingBoundarySurfaceIndex:
    index: int


@dataclass
class MovingBoundaryVolumeIndex:
    index: int


@dataclass
class FiniteVolumeIndex:
    globalIndex: int
    regionIndex: int


@dataclass
class VisPolygon:
    pointIndices: List[int]
    chomboVolumeIndex: Optional[ChomboVolumeIndex] = None
    finiteVolumeIndex: Optional[FiniteVolumeIndex] = None
    movingBoundaryVolumeIndex: Optional[MovingBoundaryVolumeIndex] = None


@dataclass
class PolyhedronFace:
    vertices: List[int]


@dataclass
class VisIrregularPolyhedron:
    polyhedronFaces: List[PolyhedronFace]
    chomboVolumeIndex: Optional[ChomboVolumeIndex] = None
    finiteVolumeIndex: Optional[FiniteVolumeIndex] = None


@dataclass
class VisVoxel:
    pointIndices: List[int]
    chomboVolumeIndex: Optional[ChomboVolumeIndex] = None
    finiteVolumeIndex: Optional[FiniteVolumeIndex] = None
    movingBoundaryVolumeIndex: Optional[MovingBoundaryVolumeIndex] = None


@dataclass
class VisTetrahedron:
    pointIndices: List[int]
    chomboVolumeIndex: Optional[ChomboVolumeIndex] = None
    finiteVolumeIndex: Optional[FiniteVolumeIndex] = None


@dataclass
class VisSurfaceTriangle:
    pointIndices: List[int]
    face: str
    chomboSurfaceIndex: Optional[ChomboSurfaceIndex] = None


@dataclass
class VisLine:
    p1: int
    p2: int
    chomboSurfaceIndex: Optional[ChomboSurfaceIndex] = None
    finiteVolumeIndex: Optional[FiniteVolumeIndex] = None
    movingBoundarySurfaceIndex: Optional[MovingBoundarySurfaceIndex] = None


@dataclass
class FiniteVolumeIndexData:
    domainName: str
    finiteVolumeIndices: List[FiniteVolumeIndex]


@dataclass
class ChomboIndexData:
    domainName: str
    chomboSurfaceIndices: Optional[List[ChomboSurfaceIndex]] = None
    chomboVolumeIndices: Optional[List[ChomboVolumeIndex]] = None


@dataclass
class MovingBoundaryIndexData:
    domainName: str
    timeIndex: int
    movingBoundarySurfaceIndices: Optional[List[MovingBoundarySurfaceIndex]] = None
    movingBoundaryVolumeIndices: Optional[List[MovingBoundaryVolumeIndex]] = None


@dataclass
class VisMesh:
    dimension: int
    origin: Vect3D
    extent: Vect3D
    points: Optional[List[VisPoint]] = None
    polygons: Optional[List[VisPolygon]] = None
    irregularPolyhedra: Optional[List[VisIrregularPolyhedron]] = None
    tetrahedra: Optional[List[VisTetrahedron]] = None
    visVoxels: Optional[List[VisVoxel]] = None
    surfaceTriangles: Optional[List[VisSurfaceTriangle]] = None
    visLines: Optional[List[VisLine]] = None
    surfacePoints: Optional[List[VisPoint]] = None


@dataclass
class Box3D:
    x_lo: float
    y_lo: float
    z_lo: float
    x_hi: float
    y_hi: float
    z_hi: float

    def to_string_key(self, precision: int = 6) -> str:
        format_string = f"%.{precision}f"
        return (f"(({format_string % self.x_lo},{format_string % self.y_lo},{format_string % self.z_lo}) : "
                f"({format_string % self.x_hi},{format_string % self.y_hi},{format_string % self.z_hi}))")

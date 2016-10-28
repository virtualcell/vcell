namespace java org.vcell.vis.vismesh.thrift
namespace py vcellvismesh

typedef i32 int
typedef i64 long

enum Face {
   Xm
   Xp
   Ym
   Yp
   Zm
   Zp
}

typedef list<int> IntList

struct Vect3D {
   1: required double x;
   2: required double y;
   3: required double z;
}
struct VisPoint {
   1: required double x;
   2: required double y;
   3: required double z;
}

typedef list<VisPoint> VisPoints

struct ChomboVolumeIndex {
   1: required int level;
   2: required int boxNumber;
   3: required int boxIndex;
   4: required double fraction;
}

struct ChomboSurfaceIndex {
   1: required int index;
}

struct MovingBoundarySurfaceIndex {
   1: required int index;
}

struct MovingBoundaryVolumeIndex {
   1: required int index;
}

struct FiniteVolumeIndex {
   1: required int globalIndex;
   2: required int regionIndex;
}

struct VisPolygon {
   1: required IntList pointIndices;
   2: optional ChomboVolumeIndex chomboVolumeIndex;
   3: optional FiniteVolumeIndex finiteVolumeIndex;
   4: optional MovingBoundaryVolumeIndex movingBoundaryVolumeIndex;
}
typedef list<VisPolygon> VisPolygons

struct PolyhedronFace {
   1: required IntList vertices;
}
typedef list<PolyhedronFace> PolyhedronFaces

struct VisIrregularPolyhedron {
   1: required PolyhedronFaces polyhedronFaces;
   2: optional ChomboVolumeIndex chomboVolumeIndex;
   3: optional FiniteVolumeIndex finiteVolumeIndex;
}
typedef list<VisIrregularPolyhedron> VisIrregularPolyhedra

struct VisVoxel {
   1: required IntList pointIndices;
   2: optional ChomboVolumeIndex chomboVolumeIndex;
   3: optional FiniteVolumeIndex finiteVolumeIndex;
   4: optional MovingBoundaryVolumeIndex movingBoundaryVolumeIndex;
}
typedef list<VisVoxel> VisVoxels

struct VisTetrahedron {
   1: required IntList pointIndices;
   2: optional ChomboVolumeIndex chomboVolumeIndex;
   3: optional FiniteVolumeIndex finiteVolumeIndex;
}
typedef list<VisTetrahedron> VisTetrahedra

struct VisSurfaceTriangle {
   1: required IntList pointIndices;
   2: required Face face; // ...............................delete????
   3: optional ChomboSurfaceIndex chomboSurfaceIndex;
}
typedef list<VisSurfaceTriangle> VisSurfaceTriangles

struct VisLine {
   1: required int p1;
   2: required int p2;
   3: optional ChomboSurfaceIndex chomboSurfaceIndex;
   4: optional FiniteVolumeIndex finiteVolumeIndex;
   5: optional MovingBoundarySurfaceIndex movingBoundarySurfaceIndex;
}
typedef list<VisLine> VisLines

struct FiniteVolumeIndexData {
   1: required string domainName;
   2: required list<FiniteVolumeIndex> finiteVolumeIndices;
}

struct ChomboIndexData {
   1: required string domainName;
   2: optional list<ChomboSurfaceIndex> chomboSurfaceIndices;
   3: optional list<ChomboVolumeIndex> chomboVolumeIndices;
}

struct MovingBoundaryIndexData {
   1: required string domainName;
   2: required int timeIndex;
   3: optional list<MovingBoundarySurfaceIndex> movingBoundarySurfaceIndices;
   4: optional list<MovingBoundaryVolumeIndex> movingBoundaryVolumeIndices;
}

struct VisMesh {
   1: required int dimension;
   2: required Vect3D origin;
   3: required Vect3D extent;
   4: optional VisPoints points;
   5: optional VisPolygons polygons;
   6: optional VisIrregularPolyhedra irregularPolyhedra;
   7: optional VisTetrahedra tetrahedra;
   8: optional VisVoxels visVoxels;
   9: optional VisSurfaceTriangles surfaceTriangles;
   10: optional VisLines visLines;
   11: optional VisPoints surfacePoints;
}

import tarfile

from pathlib import Path

from vcelldata.mesh import CartesianMesh
from vcelldata.postprocessing import PostProcessing
from vcelldata.vtk.fv_mesh_mapping import from_mesh3d_membrane, from_mesh3d_volume
from vcelldata.vtk.vtkmesh_fv import writeFiniteVolumeSmoothedVtkGridAndIndexData

test_data_dir = (Path(__file__).parent.parent / "test_data").absolute()


def extract_simdata() -> None:
    if (test_data_dir / "SimID_946368938_0_.log").exists():
        return
    with tarfile.open(test_data_dir / "SimID_946368938_simdata.tgz", 'r:gz') as tar:
        tar.extractall(path=test_data_dir)


def test_mesh_parse():
    extract_simdata()

    mesh = CartesianMesh(mesh_file=test_data_dir / "SimID_946368938_0_.mesh")
    mesh.read()

    plasma_membrane_vismesh = from_mesh3d_membrane(mesh, {0, 1, 2, 3})
    assert plasma_membrane_vismesh.dimension == 3

    cytosol_vismesh = from_mesh3d_volume(mesh, "cytosol")
    assert cytosol_vismesh.dimension == 3

    writeFiniteVolumeSmoothedVtkGridAndIndexData(plasma_membrane_vismesh, "plasma_membrane", test_data_dir / "plasma_membrane.vtu", test_data_dir / "plasma_membrane.json")
    writeFiniteVolumeSmoothedVtkGridAndIndexData(cytosol_vismesh, "cytosol", test_data_dir / "cytosol.vtu", test_data_dir / "cytosol.json")



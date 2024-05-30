import numpy as np

from vcelldata.mesh import CartesianMesh
from vcelldata.vtk.vismesh import VisMesh, VisVoxel, FiniteVolumeIndex
from vcelldata.vtk.vismesh import VisPoint, VisPolygon, Vect3D


def from_mesh_data(cartesian_mesh: CartesianMesh, domain_name: str, b_volume: bool) -> VisMesh:
    dimension = cartesian_mesh.dimension
    if dimension == 2 and b_volume:
        return from_mesh2d_volume(cartesian_mesh, domain_name)
    elif dimension == 3 and b_volume:
        return from_mesh3d_volume(cartesian_mesh, domain_name)
    elif dimension == 3 and not b_volume:
        membrane_region_ids = cartesian_mesh.get_membrane_region_ids(domain_name)
        return from_mesh3d_membrane(cartesian_mesh, membrane_region_ids)
    elif dimension == 2 and not b_volume:
        return from_mesh2d_membrane(cartesian_mesh, domain_name)
    else:
        raise Exception(f"unsupported: mesh dimension = {dimension}, volumeDomain = {b_volume}")


def to_string_key(vis_point: VisPoint, precision: int = 8) -> str:
    format_string = f"%.{precision}f"
    return f"({format_string % vis_point.x},{format_string % vis_point.y},{format_string % vis_point.z})"


def from_mesh2d_volume(self, cartesian_mesh: CartesianMesh, domain_name: str) -> VisMesh:
    # The implementation of this method is omitted for brevity.
    pass


def from_mesh2d_membrane(self, cartesian_mesh: CartesianMesh, domain_name: str) -> VisMesh:
    # The implementation of this method is omitted for brevity.
    pass


def from_mesh3d_membrane(cartesian_mesh: CartesianMesh, membrane_region_ids: set[int]) -> VisMesh:
    dimension = 3
    origin = Vect3D(cartesian_mesh.origin[0], cartesian_mesh.origin[1], cartesian_mesh.origin[2])
    extent = Vect3D(cartesian_mesh.extent[0], cartesian_mesh.extent[1], cartesian_mesh.extent[2])
    vis_mesh = VisMesh(dimension, origin, extent)
    vis_mesh.points = []
    vis_mesh.polygons = []
    curr_point_index = 0
    point_dict = {}

    # extract those membrane_elements where the mem_reg_id is in membrane_region_ids
    selected_mem_elements = [element for element in cartesian_mesh.membrane_elements if element[7] in membrane_region_ids]

    for membrane_element in selected_mem_elements:
        inside_volume_index = membrane_element[1]
        outside_volume_index = membrane_element[2]
        inside_i = inside_volume_index % cartesian_mesh.size[0]
        inside_j = (inside_volume_index % (cartesian_mesh.size[0] * cartesian_mesh.size[1])) // cartesian_mesh.size[0]
        inside_k = inside_volume_index // (cartesian_mesh.size[0] * cartesian_mesh.size[1])
        inside_box = cartesian_mesh.get_volume_element_box(inside_i, inside_j, inside_k)
        outside_i = outside_volume_index % cartesian_mesh.size[0]
        outside_j = (outside_volume_index % (cartesian_mesh.size[0] * cartesian_mesh.size[1])) // cartesian_mesh.size[0]
        outside_k = outside_volume_index // (cartesian_mesh.size[0] * cartesian_mesh.size[1])

        vis_points = []
        if inside_i == outside_i + 1:
            #  x-   z cross y
            x = inside_box.x_lo
            vis_points = [VisPoint(x, inside_box.y_lo, inside_box.z_lo),
                          VisPoint(x, inside_box.y_lo, inside_box.z_hi),
                          VisPoint(x, inside_box.y_hi, inside_box.z_hi),
                          VisPoint(x, inside_box.y_hi, inside_box.z_lo)]
        elif outside_i == inside_i + 1:
            # x+   y cross z
            x = inside_box.x_hi
            vis_points = [VisPoint(x, inside_box.y_lo, inside_box.z_lo),
                          VisPoint(x, inside_box.y_hi, inside_box.z_lo),
                          VisPoint(x, inside_box.y_hi, inside_box.z_hi),
                          VisPoint(x, inside_box.y_lo, inside_box.z_hi)]
        elif inside_j == outside_j + 1:
            # y-   x cross z
            y = inside_box.y_lo
            vis_points = [VisPoint(inside_box.x_lo, y, inside_box.z_lo),
                          VisPoint(inside_box.x_hi, y, inside_box.z_lo),
                          VisPoint(inside_box.x_hi, y, inside_box.z_hi),
                          VisPoint(inside_box.x_lo, y, inside_box.z_hi)]
        elif outside_j == inside_j + 1:
            # y+   z cross x
            y = inside_box.y_hi
            vis_points = [VisPoint(inside_box.x_lo, y, inside_box.z_lo),
                          VisPoint(inside_box.x_lo, y, inside_box.z_hi),
                          VisPoint(inside_box.x_hi, y, inside_box.z_hi),
                          VisPoint(inside_box.x_hi, y, inside_box.z_lo)]
        elif inside_k == outside_k + 1:
            # z-   y cross x
            z = inside_box.z_lo
            vis_points = [VisPoint(inside_box.x_lo, inside_box.y_lo, z),
                          VisPoint(inside_box.x_lo, inside_box.y_hi, z),
                          VisPoint(inside_box.x_hi, inside_box.y_hi, z),
                          VisPoint(inside_box.x_hi, inside_box.y_lo, z)]
        elif outside_k == inside_k + 1:
            # z+   x cross y
            z = inside_box.z_hi
            vis_points = [VisPoint(inside_box.x_lo, inside_box.y_lo, z),
                          VisPoint(inside_box.x_hi, inside_box.y_lo, z),
                          VisPoint(inside_box.x_hi, inside_box.y_hi, z),
                          VisPoint(inside_box.x_lo, inside_box.y_hi, z)]
        else:
            raise Exception("inside/outside volume indices not reconciled in membraneElement")

        indices = []
        for vis_point in vis_points:
            key = to_string_key(vis_point)
            if key not in point_dict:
                point_dict[key] = curr_point_index
                vis_mesh.points.append(vis_point)
                curr_point_index += 1
            indices.append(point_dict[key])

        vis_quad = VisPolygon(indices)
        vis_quad.finite_volume_index = FiniteVolumeIndex(membrane_element[0],
                                                         cartesian_mesh.get_membrane_region_index(membrane_element[0]))
        vis_mesh.polygons.append(vis_quad)

    return vis_mesh


def from_mesh3d_volume(cartesian_mesh: CartesianMesh, domain_name: str) -> VisMesh:
    size = cartesian_mesh.size
    num_x = size[0]
    num_y = size[1]
    num_z = size[2]
    dimension = 3

    origin = cartesian_mesh.origin
    extent = cartesian_mesh.extent
    vis_mesh = VisMesh(dimension, origin, extent)  # invoke VisMesh() constructor
    vis_mesh.points = []
    vis_mesh.visVoxels = []

    volume_region_ids = cartesian_mesh.get_volume_region_ids(domain_name)

    curr_point_index: int = 0
    point_dict: dict[str,int] = {}

    volumeIndex = 0
    for k in range(num_z):
        for j in range(num_y):
            for i in range(num_x):
                region_index: int = cartesian_mesh.volume_region_map[volumeIndex].item()
                if region_index in volume_region_ids:
                    element = cartesian_mesh.get_volume_element_box(i, j, k)
                    minX = element.x_lo
                    maxX = element.x_hi
                    minY = element.y_lo
                    maxY = element.y_hi
                    minZ = element.z_lo
                    maxZ = element.z_hi

                    """
                    points for a VisPolyhedra ... initially a hex ... then may be clipped
                    
                           p6-------------------p7
                          /|                   /|
                         / |                  / |
                       p4-------------------p5  |
                        |  |                 |  |
                        |  |                 |  |
                        |  |                 |  |         z   y
                        |  p2................|..p3        |  /
                        | /                  | /          | /
                        |/                   |/           |/
                       p0-------------------p1            O----- x
                    
                      p0 = (X-,Y-,Z-)
                      p1 = (X+,Y-,Z-)
                      p2 = (X-,Y+,Z-)
                      p3 = (X+,Y+,Z-)
                      p4 = (X-,Y-,Z+)
                      p5 = (X+,Y-,Z+)
                      p6 = (X-,Y+,Z+)
                      p7 = (X+,Y+,Z+)
                    """

                    # points for a VisVoxel
                    vis_points = [
                        [minX, minY, minZ],  # p0
                        [maxX, minY, minZ],  # p1
                        [minX, maxY, minZ],  # p2
                        [maxX, maxY, minZ],  # p3
                        [minX, minY, maxZ],  # p4
                        [maxX, minY, maxZ],  # p5
                        [minX, maxY, maxZ],  # p6
                        [maxX, maxY, maxZ],  # p7
                    ]

                    indices: list[int] = [-1] * 8
                    for v in range(8):
                        vis_point = VisPoint(vis_points[v][0], vis_points[v][1], vis_points[v][2])
                        # Assuming the __str__ method of VisPoint returns a string representation of the point
                        key = str(vis_point)
                        p = point_dict.get(key)
                        if p is None:
                            point_dict[key] = curr_point_index
                            p = curr_point_index
                            vis_mesh.points.append(vis_point)
                            curr_point_index += 1
                        indices[v] = p

                    vis_voxel = VisVoxel(indices)
                    vis_voxel.finiteVolumeIndex = FiniteVolumeIndex(volumeIndex, region_index)
                    vis_mesh.visVoxels.append(vis_voxel)
                volumeIndex += 1

    return vis_mesh

import numpy as np
from pathlib import Path
import zlib


class CartesianMesh:
    """
    reads the .mesh file and extracts the mesh information

    Example .mesh file:
    Version 1.2
    CartesianMesh {
        //              X          Y          Z
        Size           71         71         25
        Extent 74.239999999999995 74.239999999999995         26
        Origin          0          0          0

        VolumeRegionsMapSubvolume {
        6
        //VolRegID   SubvolID     Volume
                 0          0 124767.54117864356 //ec
                 1          1 14855.904388351477 //cytosol
                 2          1 1.2185460680272107 //cytosol
                 3          1 1.2185460680272107 //cytosol
                 4          1 1.2185460680272107 //cytosol
                 5          2 3673.9163951019395 //Nucleus
        }

        MembraneRegionsMapVolumeRegion {
        5
        //MemRegID    VolReg1    VolReg2    Surface
                 0          1          0 4512.8782874369472
                 1          2          0 1.7113582585034091
                 2          3          0 1.7113582585033937
                 3          4          0 1.711358258503394
                 4          5          1 1306.5985272332098
        }

        VolumeElementsMapVolumeRegion {
        126025 Compressed
        789CEDDD8D72DBC81100612389DFFF9573572A5912B9BF2066A66176B32A57B12CE22B8022E5DD11
        F5EB9799999999999999999999999999999999999999999999999999999999999999999999999999
        ...
    """
    mesh_file: Path
    size: list[int]                                      # [x, y, z]
    extent: list[float]                                  # [x, y, z]
    origin: list[float]                                  # [x, y, z]
    volume_regions: list[tuple[int, int, float, str]]    # list of tuples (vol_reg_id, subvol_id, volume, region_name)
    membrane_regions: list[tuple[int, int, int, float]]  # list of tuples (mem_reg_id, vol_reg1, vol_reg2, surface)

    # membrane_element[m,:] = [idx, vol1, vol2, conn0, conn1, conn2, conn3, mem_reg_id]
    membrane_elements: np.ndarray  # shape (num_membrane_elements, 8)

    # volume_region_map[m] = vol_reg_id
    volume_region_map: np.ndarray  # shape (size[0] * size[1] * size[2],)

    def __init__(self, mesh_file: Path) -> None:
        self.mesh_file = mesh_file
        self.size = []
        self.extent = []
        self.origin = []
        self.volume_regions = []
        self.membrane_regions = []
        # self.membrane_elements
        self.volume_region_map = np.array([], dtype=np.uint8)

    def read(self) -> None:
        # read file as lines and parse
        with (self.mesh_file.open('r') as f):
            # get line enumerator from f

            iter_lines = iter(f.readlines())
            assert next(iter_lines) == "Version 1.2\n"
            assert next(iter_lines) == "CartesianMesh {\n"
            assert next(iter_lines) == "\t//              X          Y          Z\n"

            size_line = next(iter_lines).split()
            if size_line[0] == "Size":
                self.size = [int(size_line[1]), int(size_line[2]), int(size_line[3])]

            extent_line = next(iter_lines).split()
            if extent_line[0] == "Extent":
                self.extent = [float(extent_line[1]), float(extent_line[2]), float(extent_line[3])]

            origin_line = next(iter_lines).split()
            if origin_line[0] == "Origin":
                self.origin = [float(origin_line[1]), float(origin_line[2]), float(origin_line[3])]

            while next(iter_lines) != "\tVolumeRegionsMapSubvolume {\n":
                pass
            num_volume_regions = int(next(iter_lines))
            header_line = next(iter_lines)
            self.volume_regions = []
            for i in range(num_volume_regions):
                parts = next(iter_lines).split()
                self.volume_regions.append((int(parts[0]), int(parts[1]), float(parts[2]), parts[3].strip("//")))

            while next(iter_lines) != "\tMembraneRegionsMapVolumeRegion {\n":
                pass
            num_membrane_regions = int(next(iter_lines))
            header_line = next(iter_lines)
            self.membrane_regions = []
            for i in range(num_membrane_regions):
                parts = next(iter_lines).split()
                self.membrane_regions.append((int(parts[0]), int(parts[1]), int(parts[2]), float(parts[3])))

            while next(iter_lines) != "\tVolumeElementsMapVolumeRegion {\n":
                pass
            compressed_line = next(iter_lines).split()
            num_volume_elements = int(compressed_line[0])
            assert compressed_line[1] == "Compressed"
            # read HEX lines until "}" line, and concatenate into one string, then convert to bytes and decompress
            hex_lines = []
            while True:
                line = next(iter_lines)
                if line.strip() == "}":
                    break
                hex_lines.append(line.strip())
            hex_string: str = "".join(hex_lines).strip()
            compressed_bytes = bytes.fromhex(hex_string)
            # assert len(compressed_bytes) == num_compressed_bytes
            uncompressed_bytes: bytes = zlib.decompress(compressed_bytes)
            self.volume_region_map = np.frombuffer(uncompressed_bytes, dtype='<u2')  # unsigned 2-byte integers
            assert self.volume_region_map.shape[0] == self.size[0] * self.size[1] * self.size[2]
            assert num_volume_elements == self.volume_region_map.shape[0]
            assert set(np.unique(self.volume_region_map)) == set([v[0] for v in self.volume_regions])

            while next(iter_lines).strip() != "MembraneElements {":
                pass
            num_membrane_elements = int(next(iter_lines))
            self.membrane_elements = np.zeros((num_membrane_elements, 8), dtype=np.int32)
            header_line = next(iter_lines).split()
            mem_index = 0
            while True:
                line = next(iter_lines)
                if line.strip() == "}":
                    break
                parts = line.split()
                idx = int(parts[0])
                vol1 = int(parts[1])
                vol2 = int(parts[2])
                conn0 = int(parts[3])
                conn1 = int(parts[4])
                conn2 = int(parts[5])
                conn3 = int(parts[6])
                mem_reg_id = int(parts[7])
                self.membrane_elements[mem_index, :] = [idx, vol1, vol2, conn0, conn1, conn2, conn3, mem_reg_id]
                mem_index += 1
            assert self.membrane_elements.shape == (num_membrane_elements, 8)
            assert set(np.unique(self.membrane_elements[:, 7])) == set([v[0] for v in self.membrane_regions])

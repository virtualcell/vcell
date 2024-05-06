from pathlib import Path

import numpy as np
import zarr

from vcelldata.mesh import CartesianMesh
from vcelldata.simdata_models import PdeDataSet, DataBlockHeader, DataFunctions, NamedFunction, VariableType


def write_zarr(pde_dataset: PdeDataSet, data_functions: DataFunctions, mesh: CartesianMesh, zarr_dir: Path) -> None:

    volume_data_vars: list[DataBlockHeader] = [v for v in pde_dataset.variables_block_headers()
                                               if v.variable_type == VariableType.VOLUME]
    volume_functions: list[NamedFunction] = [f for f in data_functions.named_functions
                                             if f.variable_type == VariableType.VOLUME]
    num_channels = len(volume_data_vars) + len(volume_functions) + 1
    num_t: int = len(pde_dataset.times())
    times: list[float] = pde_dataset.times()
    header = pde_dataset.first_data_zip_file_metadata().file_header
    num_x: int = header.sizeX
    num_y: int = header.sizeY
    num_z: int = header.sizeZ

    z1 = zarr.open(str(zarr_dir.absolute()), mode='w', shape=(num_t, num_channels, num_z, num_y, num_x), chunks=(1,1,num_z,num_y,num_x), dtype=float)

    channel_metadata: list[dict] = []
    for t in range(num_t):
        bindings = {}
        # add region map
        region_map = mesh.volume_region_map.reshape((num_z, num_y, num_x))
        z1[t, 0, :, :, :] = region_map
        if t == 0:
            channel_metadata.append({"index": 0,
                                 "label": "region_mask",
                                 "domain_name": "all",
                                 "min_value": np.min(region_map),
                                 "max_value": np.max(region_map)})

        # add volumetric state variables
        for i, v in enumerate(volume_data_vars):
            var_data: np.ndarray = pde_dataset.get_data(v.var_name, times[t]).reshape((num_z, num_y, num_x))
            c = i + 1
            z1[t, c, :, :, :] = var_data
            domain_name = v.var_name.split("::")[0]
            var_name = v.var_name.split("::")[1]
            bindings[var_name] = var_data
            if t == 0:
                channel_metadata.append({"index": c,
                                         "label": var_name,
                                         "domain_name": domain_name,
                                         "min_values": [],
                                         "max_values": [],
                                         "mean_values": []})
            channel_metadata[c]["min_values"].append(np.min(var_data))
            channel_metadata[c]["max_values"].append(np.max(var_data))
            channel_metadata[c]["mean_values"].append(np.mean(var_data))

        # add volumetric functions
        for j, f in enumerate(volume_functions):
            func_data = f.evaluate(variable_bindings=bindings).reshape((num_z, num_y, num_x))
            c = i + j + 2
            z1[t, c, :, :, :] = func_data
            domain_name = f.name.split("::")[0]
            function_name = f.name.split("::")[1]
            if t == 0:
                channel_metadata.append({"index": (i + j + 2),
                                         "label": function_name,
                                         "domain_name": domain_name,
                                         "min_values": [],
                                         "max_values": [],
                                         "mean_values": []})
            channel_metadata[c]["min_values"].append(np.min(func_data))
            channel_metadata[c]["max_values"].append(np.max(func_data))
            channel_metadata[c]["mean_values"].append(np.mean(func_data))

    z1.attrs["metadata"] = {
        "axes": [
            {"name": "t", "type": "time", "unit": "second"},
            {"name": "c", "type": "channel", "unit": None},
            {"name": "z", "type": "space", "unit": "micrometer"},
            {"name": "y", "type": "space", "unit": "micrometer"},
            {"name": "x", "type": "space", "unit": "micrometer"}
        ],
        "channels": channel_metadata,
        "times": times,
        "mesh": {
            "size": mesh.size,
            "extent": mesh.extent,
            "origin": mesh.origin,
            "volume_regions": [{"region_index": mesh.volume_regions[i][0],
                               "domain_type_index": mesh.volume_regions[i][1],
                               "volume": mesh.volume_regions[i][2],
                               "domain_name": mesh.volume_regions[i][3]} for i in range(len(mesh.volume_regions))],
        }
    }
    z1.attrs["metadata"]["mesh"] = {
        "size": mesh.size,
        "extent": mesh.extent,
        "origin": mesh.origin,
        "volume_regions": [{"region_index": mesh.volume_regions[i][0],
                           "domain_type_index": mesh.volume_regions[i][1],
                           "volume": mesh.volume_regions[i][2],
                           "domain_name": mesh.volume_regions[i][3]} for i in range(len(mesh.volume_regions))],
    }

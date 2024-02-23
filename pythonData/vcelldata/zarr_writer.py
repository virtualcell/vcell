from pathlib import Path

import numpy as np
import zarr

from vcelldata.simdata_models import PdeDataSet, DataBlockHeader, DataFunctions, NamedFunction, VariableType


def write_zarr(pde_dataset: PdeDataSet, data_functions: DataFunctions, zarr_dir: Path) -> None:

    volume_data_vars: list[DataBlockHeader] = [v for v in pde_dataset.variables_block_headers()
                                               if v.variable_type == VariableType.VOLUME]
    volume_functions: list[NamedFunction] = [f for f in data_functions.named_functions
                                             if f.variable_type == VariableType.VOLUME]
    num_channels = len(volume_data_vars) + len(volume_functions)
    num_t: int = len(pde_dataset.times())
    times: list[float] = pde_dataset.times()
    header = pde_dataset.first_data_zip_file_metadata().file_header
    num_x: int = header.sizeX
    num_y: int = header.sizeY
    num_z: int = header.sizeZ

    z1 = zarr.open(str(zarr_dir.absolute()), mode='w', shape=(num_t, num_channels, num_z, num_y, num_x), chunks=(1,1,num_z,num_y,num_x), dtype=float)

    for t in range(num_t):
        bindings = {}
        for i, v in enumerate(volume_data_vars):
            var_data: np.ndarray = pde_dataset.get_data(v.var_name, times[t]).reshape((num_z, num_y, num_x))
            z1[t, i, :, :, :] = var_data
            var_name = v.var_name.split("::")[1]
            bindings[var_name] = var_data
        for j, f in enumerate(volume_functions):
            func_data = f.evaluate(variable_bindings=bindings).reshape((num_z, num_y, num_x))
            z1[t, i + j + 1, :, :, :] = func_data

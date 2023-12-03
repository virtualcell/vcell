import copy
from functools import partial
from pathlib import Path
from zipfile import ZipFile

import h5py
import numpy as np


def _get_ds_dictionaries(ds_dict: dict[str, np.ndarray], _name, node):
    # From https://stackoverflow.com/questions/70055365/hdf5-file-to-dictionary
    fullname = node.name
    if isinstance(node, h5py.Dataset):
        # node is a dataset
        # print(f"Dataset: {fullname}; adding to dictionary")
        ds_dict[fullname] = np.array(node)
        # print('ds_dict size', len(ds_dict))
    # else:
        # node is a group
        # print(f'Group: {fullname}; skipping')


def get_results(results_zip_file: Path) -> dict[str, dict[str, np.ndarray]]:
    results: dict[str, dict[str, np.ndarray]] = {}
    with ZipFile(results_zip_file, "r") as the_zip:
        for name in the_zip.namelist():
            if "reports.h5" in name:
                with h5py.File(the_zip.open(name)) as h5:
                    ds_dict: dict[str, np.ndarray] = {}
                    ds_visitor = partial(_get_ds_dictionaries, ds_dict)
                    h5.visititems(ds_visitor)
                    results[name] = copy.deepcopy(ds_dict)
    return results


def compare_arrays(arr1: np.ndarray, arr2: np.ndarray) -> bool:
    if type(arr1[0]) == np.float64:
        max1 = max(arr1)
        max2 = max(arr2)
        atol = max(1e-3, max1*1e-5, max2*1e-5)
        return np.allclose(arr1, arr2, rtol=1e-4, atol=atol)
    for n in range(len(arr1)):
        if not compare_arrays(arr1[n], arr2[n]):
            return False
    return True


def compare_datasets(results1: dict[str, dict[str, np.ndarray]], results2: dict[str, dict[str, np.ndarray]]) -> bool:
    for h5_file_path in results1:
        if h5_file_path not in results2:
            return False
        for dataset_name in results1[h5_file_path]:
            if dataset_name not in results2[h5_file_path]:
                return False
            arr1 = results1[h5_file_path][dataset_name]
            arr2 = results2[h5_file_path][dataset_name]
            if arr1.shape != arr2.shape:
                return False
            if not compare_arrays(arr1, arr2):
                return False
    return True

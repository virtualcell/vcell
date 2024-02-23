import shutil
from pathlib import Path

import pytest

from tests.vcell_parsing_test import extract_simdata
from vcelldata.simdata_models import PdeDataSet, DataFunctions
from vcelldata.zarr_writer import write_zarr

test_data_dir = (Path(__file__).parent.parent / "test_data").absolute()

top_level_zattrs = """
{
    "multiscales": [
        {
            "axes": [
                {
                    "name": "t",
                    "type": "time",
                    "unit": "millisecond"
                },
                {
                    "name": "c",
                    "type": "channel",
                    "unit": null
                },
                {
                    "name": "z",
                    "type": "space",
                    "unit": "micrometer"
                },
                {
                    "name": "y",
                    "type": "space",
                    "unit": "micrometer"
                },
                {
                    "name": "x",
                    "type": "space",
                    "unit": "micrometer"
                }
            ],
            "coordinateTransformations": null,
            "datasets": [
                {
                    "coordinateTransformations": [
                        {
                            "scale": [
                                1,
                                1,
                                0.5,
                                0.325,
                                0.325
                            ],
                            "type": "scale"
                        },
                        {
                            "translation": [
                                0.0,
                                0.0,
                                0.0,
                                0.0,
                                0.0
                            ],
                            "type": "translation"
                        }
                    ],
                    "path": "0"
                },
                {
                    "coordinateTransformations": [
                        {
                            "scale": [
                                1,
                                1,
                                0.5,
                                0.65,
                                0.65
                            ],
                            "type": "scale"
                        },
                        {
                            "translation": [
                                0.0,
                                0.0,
                                0.0,
                                0.0,
                                0.0
                            ],
                            "type": "translation"
                        }
                    ],
                    "path": "1"
                },
                {
                    "coordinateTransformations": [
                        {
                            "scale": [
                                1,
                                1,
                                0.5,
                                1.3,
                                1.3
                            ],
                            "type": "scale"
                        },
                        {
                            "translation": [
                                0.0,
                                0.0,
                                0.0,
                                0.0,
                                0.0
                            ],
                            "type": "translation"
                        }
                    ],
                    "path": "2"
                },
                {
                    "coordinateTransformations": [
                        {
                            "scale": [
                                1,
                                1,
                                0.5,
                                2.6,
                                2.6
                            ],
                            "type": "scale"
                        },
                        {
                            "translation": [
                                0.0,
                                0.0,
                                0.0,
                                0.0,
                                0.0
                            ],
                            "type": "translation"
                        }
                    ],
                    "path": "3"
                },
                {
                    "coordinateTransformations": [
                        {
                            "scale": [
                                1,
                                1,
                                0.5,
                                5.2,
                                5.2
                            ],
                            "type": "scale"
                        },
                        {
                            "translation": [
                                0.0,
                                0.0,
                                0.0,
                                0.0,
                                0.0
                            ],
                            "type": "translation"
                        }
                    ],
                    "path": "4"
                }
            ],
            "name": "/",
            "version": "0.4"
        }
    ],
    "omero": {
        "channels": [
            {
                "active": true,
                "coefficient": 1,
                "color": "ffffff",
                "family": "linear",
                "inverted": false,
                "label": "EM B525/50",
                "window": {
                    "end": 1.0,
                    "max": 1.0,
                    "min": 0.0,
                    "start": 0.0
                }
            },
            {
                "active": true,
                "coefficient": 1,
                "color": "ffffff",
                "family": "linear",
                "inverted": false,
                "label": "DM A561LP",
                "window": {
                    "end": 1.0,
                    "max": 1.0,
                    "min": 0.0,
                    "start": 0.0
                }
            },
            {
                "active": true,
                "coefficient": 1,
                "color": "ffffff",
                "family": "linear",
                "inverted": false,
                "label": "488",
                "window": {
                    "end": 1.0,
                    "max": 1.0,
                    "min": 0.0,
                    "start": 0.0
                }
            },
            {
                "active": true,
                "coefficient": 1,
                "color": "ffffff",
                "family": "linear",
                "inverted": false,
                "label": "640",
                "window": {
                    "end": 1.0,
                    "max": 1.0,
                    "min": 0.0,
                    "start": 0.0
                }
            }
        ],
        "id": 1,
        "name": "3500006064_20X_water002_mama_bear",
        "rdefs": {
            "defaultT": 0,
            "defaultZ": 20,
            "model": "color"
        },
        "version": "0.4"
    }
}
"""


def test_zarr_writer():
    extract_simdata()

    sim_data_dir = test_data_dir
    sim_id = 946368938
    job_id = 0
    pde_dataset = PdeDataSet(base_dir=sim_data_dir, log_filename=f"SimID_{sim_id}_{job_id}_.log")
    pde_dataset.read()
    data_functions = DataFunctions(function_file=sim_data_dir / f"SimID_{sim_id}_{job_id}_.functions")
    data_functions.read()

    write_zarr(pde_dataset=pde_dataset, data_functions=data_functions, zarr_dir=test_data_dir / "zarr")

    # TODO: verify the written data

    # recursively remove files and directories from path (equivalent to rm -rf) for test_data_dir / "zarr"
    shutil.rmtree(test_data_dir / "zarr")

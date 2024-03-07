from enum import IntEnum
from pathlib import Path

import numpy as np
from h5py import Dataset, File as H5File, Group


class StatisticType(IntEnum):
    AVERAGE = 0
    TOTAL = 1
    MIN = 2
    MAX = 3


class ImageMetadata:
    name: str
    group_path: str
    extents: np.ndarray
    origin: np.ndarray
    shape: tuple[int]

    def __init__(self, name: str, group_path: str):
        self.name = name
        self.group_path = group_path

    def get_dataset(self, hdf5File: H5File, time_index: int) -> Dataset:
        image_group: Group = hdf5File[self.group_path]
        image_ds: Dataset = image_group[f'time{time_index:06d}']
        return image_ds

    def read(self, f: H5File):
        image_group: Group = f[self.group_path]

        # get attributes from the group
        extents_list = []
        origin_list = []
        if "ExtentX" in image_group.attrs:
            extents_list.append(image_group.attrs['ExtentX'])
        if "ExtentY" in image_group.attrs:
            extents_list.append(image_group.attrs['ExtentY'])
        if "ExtentZ" in image_group.attrs:
            extents_list.append(image_group.attrs['ExtentZ'])
        if "OriginX" in image_group.attrs:
            origin_list.append(image_group.attrs['OriginX'])
        if "OriginY" in image_group.attrs:
            origin_list.append(image_group.attrs['OriginY'])
        if "OriginZ" in image_group.attrs:
            origin_list.append(image_group.attrs['OriginZ'])
        self.extents = np.array(extents_list)
        self.origin = np.array(origin_list)
        self.shape = self.get_dataset(f, 0).shape


class VariableInfo:
    var_index: int
    var_name: str  # e.g. "C_cyt"
    stat_channel: int
    statistic_type: StatisticType  # e.g. StatisticType.AVERAGE
    stat_var_name: str  # e.g. "C_cyt_average"
    stat_var_unit: str      # e.g. "uM"

    def __init__(self, stat_var_name: str, stat_var_unit: str, stat_channel: int, var_index: int):
        self.stat_var_name = stat_var_name
        self.stat_var_unit = stat_var_unit
        self.stat_channel = stat_channel
        self.var_index = var_index
        # stat_var_name is in the form of "C_cyt_average" so remove _average to get the variable name
        stat_type_raw = stat_var_name.split("_")[-1]
        self.statistic_type = StatisticType[stat_type_raw.upper()]
        self.var_name = stat_var_name.replace("_"+stat_type_raw, "")


class PostProcessing:
    postprocessing_hdf5_path: Path
    times: np.ndarray
    variables: list[VariableInfo]
    statistics: np.ndarray  # shape (times, vars, stats) where status is average=0, total=1, min=2, max=3
    image_metadata: list[ImageMetadata]

    def __init__(self, postprocessing_hdf5_path: Path):
        self.postprocessing_hdf5_path = postprocessing_hdf5_path
        self.variables = []
        self.image_metadata = []


    def read(self) -> None:
        # read the file as hdf5
        with H5File(self.postprocessing_hdf5_path, 'r') as f:
            # read dataset with path /PostProcessing/Times
            times_ds: Dataset = f['/PostProcessing/Times']
            # read array from times dataset into a ndarray
            self.times = times_ds[()]

            # read attributes from group /PostProcessing/VariableStatistics
            # data is flat, so we can read the attributes directly, so name and units for each channel are separate
            #
            # key=comp_0_name, value=b'C_cyt_average'
            # key=comp_0_unit, value=b'uM'
            # key=comp_1_name, value=b'C_cyt_total'
            # key=comp_1_unit, value=b'molecules'
            # key=comp_2_name, value=b'C_cyt_min'
            # key=comp_2_unit, value=b'uM'
            # key=comp_3_name, value=b'C_cyt_max'
            # key=comp_3_unit, value=b'uM'
            #
            var_stats_grp = f['/PostProcessing/VariableStatistics']
            # gather stat_var_name and stat_var_unit for each channel into dictionaries by channel
            var_name_by_channel: dict[int, str] = {}
            var_unit_by_channel: dict[int, str] = {}
            for k, v in var_stats_grp.attrs.items():
                parts = k.split('_')
                channel = int(parts[1])
                value = v.decode('utf-8')
                if parts[2] == "name":
                    var_name_by_channel[channel] = value
                elif parts[2] == "unit":
                    var_unit_by_channel[channel] = value
            # combine into a single list of VariableInfo objects, one for each channel
            self.variables = [VariableInfo(stat_var_name=var_name_by_channel[i],
                                           stat_var_unit=var_unit_by_channel[i],
                                           stat_channel=i,
                                           var_index=i//4)
                              for i in range(len(var_name_by_channel))]

            # within /PostProcessing/VariableStatistics, there are datasets for each time point
            # PostProcessing/VariableStatistics
            # PostProcessing/VariableStatistics/time000000
            # PostProcessing/VariableStatistics/time000001
            # PostProcessing/VariableStatistics/time000002
            # PostProcessing/VariableStatistics/time000003
            # PostProcessing/VariableStatistics/time000004

            # we can read the data for each time point into a list of ndarrays
            statistics_raw: np.ndarray = np.zeros((len(self.times), len(self.variables)))
            for time_index in range(len(self.times)):
                time_ds: Dataset = var_stats_grp[f'time{time_index:06d}']
                statistics_raw[time_index, :] = time_ds[()]

            # reshape the statistics_raw into a 3D array (times, vars, stats)
            self.statistics = statistics_raw.reshape((len(self.times), len(self.variables)//4, 4))

            # get list of child groups from /PostProcessing which are not Times or VariableStatistics
            # e.g. /PostProcessing/fluor
            image_groups = [k for k in f['/PostProcessing'].keys() if k not in ['Times', 'VariableStatistics']]

            # for each image group, read the metadata to allow reading later
            for image_group in image_groups:
                metadata = ImageMetadata(group_path=f"/PostProcessing/{image_group}", name=image_group)
                metadata.read(f)
                self.image_metadata.append(metadata)

    def read_image_data(self, image_metadata: ImageMetadata, time_index: int) -> np.ndarray:
        with H5File(self.postprocessing_hdf5_path, 'r') as f:
            image_ds: Dataset = image_metadata.get_dataset(hdf5File=f, time_index=time_index)
            return image_ds[()]

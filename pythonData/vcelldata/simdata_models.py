import ast
from enum import Enum
from pathlib import Path
from typing import IO
from zipfile import ZipFile

import numexpr as ne
import numpy
import numpy as np

PYTHON_ENDIANNESS = 'big'
NUMPY_FLOAT_DTYPE = ">f8"


class SpecialLogFileType(Enum):
    IDA_DATA_IDENTIFIER = "IDAData logfile"
    ODE_DATA_IDENTIFIER = "ODEData logfile"
    NETCDF_DATA_IDENTIFIER = "NetCDFData logfile"
    MOVINGBOUNDARY_DATA_IDENTIFIER = "MBSData"
    COMSOLE_DATA_IDENTIFIER = "COMSOL"

    @staticmethod
    def from_string(s: str):
        for special_log_file_type in SpecialLogFileType:
            if s == special_log_file_type.value:
                return special_log_file_type
        return None


class DomainType(Enum):
    POSTPROCESSING = "PostProcessing"
    UNKNOWN = "Unknown"
    VOLUME = "Volume"
    MEMBRANE = "Membrane"
    CONTOUR = "Contour"
    NONSPATIAL = "Nonspatial"
    POINT = "Point"


class VariableType(Enum):
    UNKNOWN = 0
    VOLUME = 1
    MEMBRANE = 2
    CONTOUR = 3
    VOLUME_REGION = 4
    MEMBRANE_REGION = 5
    CONTOUR_REGION = 6
    NONSPATIAL = 7
    VOLUME_PARTICLE = 8
    MEMBRANE_PARTICLE = 9
    POINT_VARIABLE = 10
    POSTPROCESSING = 999

    @staticmethod
    def from_string(s: str):
        switcher = {
            "Unknown": VariableType.UNKNOWN,
            "Volume_VariableType": VariableType.VOLUME,
            "Membrane_VariableType": VariableType.MEMBRANE,
            "Contour_VariableType": VariableType.CONTOUR,
            "Volume_Region_VariableType": VariableType.VOLUME_REGION,
            "Membrane_Region_VariableType": VariableType.MEMBRANE_REGION,
            "Contour_Region_VariableType": VariableType.CONTOUR_REGION,
            "Nonspatial_VariableType": VariableType.NONSPATIAL,
            "Volume_Particle_VariableType": VariableType.VOLUME_PARTICLE,
            "Membrane_Particle_VariableType": VariableType.MEMBRANE_PARTICLE,
            "Point_Variable_VariableType": VariableType.POINT_VARIABLE,
            "PostProcessing_VariableType": VariableType.POSTPROCESSING
        }
        return switcher.get(s, VariableType.UNKNOWN)


class DataFileHeader:
    magic_string: str
    version_string: str
    num_blocks: int
    first_block_offset: int
    sizeX: int
    sizeY: int
    sizeZ: int

    def read(self, f: IO[bytes]) -> int:
        read_count = 0
        self.magic_string = f.read(16).decode('utf-8').split('\x00')[0]
        read_count += 16
        self.version_string = f.read(8).decode('utf-8').split('\x00')[0]
        read_count += 8
        self.num_blocks = int.from_bytes(f.read(4), byteorder=PYTHON_ENDIANNESS)
        read_count += 4
        self.first_block_offset = int.from_bytes(f.read(4), byteorder=PYTHON_ENDIANNESS)
        read_count += 4
        self.sizeX = int.from_bytes(f.read(4), byteorder=PYTHON_ENDIANNESS)
        read_count += 4
        self.sizeY = int.from_bytes(f.read(4), byteorder=PYTHON_ENDIANNESS)
        read_count += 4
        self.sizeZ = int.from_bytes(f.read(4), byteorder=PYTHON_ENDIANNESS)
        read_count += 4

        return read_count


class VariableInfo:
    var_name: str
    variable_type: VariableType


class DataBlockHeader:
    var_name: VariableInfo
    variable_type: VariableType
    size: int
    data_offset: int

    def read(self, f: IO[bytes]) -> int:
        read_count = 0
        self.var_name: str = f.read(124).decode('utf-8').split('\x00')[0]
        read_count += 124
        self.variable_type = VariableType(int.from_bytes(f.read(4), byteorder=PYTHON_ENDIANNESS))
        read_count += 4
        self.size = int.from_bytes(f.read(4), byteorder=PYTHON_ENDIANNESS)
        read_count += 4
        self.data_offset = int.from_bytes(f.read(4), byteorder=PYTHON_ENDIANNESS)
        read_count += 4
        return read_count


class DataZipFileMetadata:
    zip_file: Path
    zip_entry: str
    file_header: DataFileHeader
    data_blocks: list[DataBlockHeader]

    # constructor
    def __init__(self, zip_file: Path, zip_entry: str) -> None:
        self.zip_file = zip_file
        self.zip_entry = zip_entry

    def read(self) -> None:
        with ZipFile(self.zip_file, 'r') as zip:
            with zip.open(self.zip_entry) as f:
                self.file_header = DataFileHeader()
                self.file_header.read(f)
                blocks = []
                for _ in range(self.file_header.num_blocks):
                    data_block = DataBlockHeader()
                    data_block.read(f)
                    blocks.append(data_block)
                self.data_blocks = blocks

    def get_data_block_header(self, variable) -> DataBlockHeader:
        for db in self.data_blocks:
            if db.var_name == variable:
                return db
        raise ValueError(f"Variable {variable} not found in zip entry {self.zip_entry}")


class PdeDataSet:
    base_dir: Path
    log_filename: str
    data_filenames: list[str]
    zip_filenames: list[str]
    data_times: list[float]
    data_zip_file_metadata: dict[float, DataZipFileMetadata]

    def __init__(self, base_dir: Path, log_filename: str) -> None:
        self.base_dir = base_dir
        self.log_filename = log_filename
        self.data_filenames = []
        self.zip_filenames = []
        self.data_times = []
        self.data_zip_file_metadata = {}

    def read(self) -> None:
        log_file: Path = self.base_dir / self.log_filename
        with log_file.open('r') as f:
            first_line = True
            for line in f:
                if first_line:
                    # if line starts with a string from SpecialLogFileType, then it is not a standard PDE log file
                    if SpecialLogFileType.from_string(line):
                        special_log_file_type = SpecialLogFileType.from_string(line)
                        raise NotImplementedError(f"Special log file type {special_log_file_type} not implemented")
                    first_line = False
                _iteration, filename, zip_filename, time_str = line.split()
                self.data_filenames.append(filename)
                self.zip_filenames.append(zip_filename)
                self.data_times.append(float(time_str))

    def times(self) -> list[float]:
        return self.data_times

    def time_index(self, time: float) -> int:
        return self.data_times.index(time)

    def first_data_zip_file_metadata(self) -> DataZipFileMetadata:
        first_zip_entry = self.data_zip_file_metadata.get(0.0)
        if first_zip_entry is None:
            first_zip_entry = DataZipFileMetadata(self.base_dir / self.zip_filenames[0], self.data_filenames[0])
            first_zip_entry.read()
        return first_zip_entry

    def variables_block_headers(self) -> list[DataBlockHeader]:
        first_zip_entry = self.first_data_zip_file_metadata()
        if first_zip_entry is None:
            return []
        return [db for db in first_zip_entry.data_blocks]

    def _get_data_zip_file_metadata(self, time: float) -> DataZipFileMetadata:
        zip_entry = self.data_zip_file_metadata.get(time)
        if zip_entry is None:
            time_index = self.time_index(time)
            zip_file_path = self.base_dir / self.zip_filenames[time_index]
            zip_entry = DataZipFileMetadata(zip_file_path, self.data_filenames[time_index])
            zip_entry.read()
            self.data_zip_file_metadata[time] = zip_entry
        return zip_entry

    def get_data(self, variable: str, time: float) -> numpy.ndarray:
        zip_file_entry: DataZipFileMetadata = self._get_data_zip_file_metadata(time)
        data_block_header: DataBlockHeader = zip_file_entry.get_data_block_header(variable)

        with (ZipFile(zip_file_entry.zip_file, 'r') as zip):
            with zip.open(zip_file_entry.zip_entry, mode='r') as f:
                f.seek(data_block_header.data_offset)
                buffer = bytearray(0)
                bytes_left_to_read = data_block_header.size * 8
                while bytes_left_to_read > 0:
                    bytes_read = f.read(bytes_left_to_read)
                    buffer.extend(bytes_read)
                    bytes_left_to_read -= len(bytes_read)
                array = np.frombuffer(buffer, dtype=NUMPY_FLOAT_DTYPE)
                return array


class NamedFunction:
    name: str
    vcell_expression: str
    python_expression: str
    variables: list[str]
    variable_type: VariableType

    def __init__(self, name: str, vcell_expression: str, variable_type: VariableType) -> None:
        self.name = name
        self.vcell_expression = vcell_expression
        self.python_expression = vcell_expression.replace("^", "**").lstrip(" ").rstrip(" ")
        self.variable_type = variable_type

        # Parse the python expression into an AST and extract all Name nodes (which represent variables)
        tree = ast.parse(self.python_expression)
        self.variables = [node.id for node in ast.walk(tree) if isinstance(node, ast.Name)]

    def evaluate(self, variable_bindings: dict[str, np.ndarray]) -> np.ndarray:
        ne.set_num_threads(1)
        expression = self.python_expression
        return ne.evaluate(expression, local_dict=variable_bindings)

    def __str__(self):
        return f"NamedFunction(name={self.name}, vcell_expression={self.vcell_expression}, python_expression={self.vcell_expression}, variable_type={self.variable_type}, variables={self.variables}"


class DataFunctions:
    function_file: Path
    named_functions: list[NamedFunction]

    def __init__(self, function_file: Path) -> None:
        self.function_file = function_file
        self.named_functions = []

    def read(self) -> None:
        with self.function_file.open('r') as f:
            # skip lines beginning with # and blank lines
            for line in f:
                if line.startswith('#') or line.isspace():
                    continue
                # read each named function from one line
                # example line: "cytosol::J_r0; (RanC_cyt - (1000.0 * C_cyt * Ran_cyt)); ; Volume_VariableType; false"
                parts = line.split(';')
                name = parts[0].strip(" ")
                expression = parts[1].strip(" ")
                _unknown_skipped = parts[2]
                variable_type = VariableType.from_string(parts[3].strip(" "))
                _boolean_skipped = parts[4]
                function = NamedFunction(name=name, vcell_expression=expression, variable_type=variable_type)
                self.named_functions.append(function)

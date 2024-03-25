import tarfile
from pathlib import Path

import numpy as np

from vcelldata.mesh import CartesianMesh
from vcelldata.postprocessing import ImageMetadata, PostProcessing, VariableInfo, StatisticType
from vcelldata.simdata_models import PdeDataSet, DataFunctions, NamedFunction, VariableType

test_data_dir = (Path(__file__).parent.parent / "test_data").absolute()


def extract_simdata() -> None:
    if (test_data_dir / "SimID_946368938_0_.log").exists():
        return
    with tarfile.open(test_data_dir / "SimID_946368938_simdata.tgz", 'r:gz') as tar:
        tar.extractall(path=test_data_dir)


def test_parse_vcelldata():
    extract_simdata()

    pde_dataset = PdeDataSet(base_dir=test_data_dir, log_filename="SimID_946368938_0_.log")
    pde_dataset.read()

    expected_times = [0.0, 0.25, 0.5, 0.75, 1.0]
    assert pde_dataset.times() == expected_times

    expected_variables = [
        'cytosol::C_cyt',
         'cytosol::Ran_cyt',
         'cytosol::RanC_cyt',
         'Nucleus::RanC_nuc',
         'vcRegionVolume',
         'vcRegionArea',
         'vcRegionVolume_ec',
         'vcRegionVolume_cytosol',
         'vcRegionVolume_Nucleus'
    ]
    assert [v.var_name for v in pde_dataset.variables_block_headers()] == expected_variables

    expected_shapes = [(126025,), (126025,), (126025,), (126025,), (6,), (5,), (5,), (5,), (5,)]
    assert [pde_dataset.get_data(v.var_name, 0.0).shape for v in pde_dataset.variables_block_headers()] == expected_shapes

    expected_min_C_cyt = [0.0, 0.0, 0.0, 0.0, 0.0]
    assert [np.min(pde_dataset.get_data('cytosol::C_cyt', t)) for t in pde_dataset.times()] == expected_min_C_cyt

    expected_max_C_cyt = [0.0, 1.6578610937269188e-05, 3.688810690038264e-05, 5.838639163921412e-05, 7.973304853048764e-05]
    assert [np.max(pde_dataset.get_data('cytosol::C_cyt', t)) for t in pde_dataset.times()] == expected_max_C_cyt

    # data = pde_dataset.get_data('cytosol::C_cyt', 1.0)
    for t in pde_dataset.times():
        for v in pde_dataset.variables_block_headers():
            data = pde_dataset.get_data(v.var_name, t)
            if data.size > 0 and v == "cytosol::RanC_cyt":
                print(f"v={v}, t={t}, shape={data.shape}, min={np.min(data)}, max={np.max(data)}")


def test_function_parse():
    extract_simdata()

    data_functions = DataFunctions(function_file=test_data_dir / "SimID_946368938_0_.functions")
    data_functions.read()

    expected_functions = [
        NamedFunction(name="Nucleus_cytosol_membrane::J_flux0",
                      vcell_expression="(2.0 * (RanC_cyt - RanC_nuc))",
                      variable_type=VariableType.MEMBRANE),
        NamedFunction(name="cytosol::J_r0",
                      vcell_expression="(RanC_cyt - (1000.0 * C_cyt * Ran_cyt))",
                      variable_type=VariableType.VOLUME),
        NamedFunction(name="cytosol_ec_membrane::s2",
                      vcell_expression="0.0",
                      variable_type=VariableType.MEMBRANE_REGION),
        NamedFunction(name="cytosol::Size_cyt",
                      vcell_expression="vcRegionVolume('cytosol')",
                      variable_type=VariableType.VOLUME_REGION),
        NamedFunction(name="ec::Size_EC",
                      vcell_expression="vcRegionVolume('ec')",
                      variable_type=VariableType.VOLUME_REGION),
        NamedFunction(name="Nucleus_cytosol_membrane::Size_nm",
                      vcell_expression="vcRegionArea('Nucleus_cytosol_membrane')",
                      variable_type=VariableType.MEMBRANE_REGION),
        NamedFunction(name="Nucleus::Size_nuc",
                      vcell_expression="vcRegionVolume('Nucleus')",
                      variable_type=VariableType.VOLUME_REGION),
        NamedFunction(name="cytosol_ec_membrane::Size_pm",
                      vcell_expression="vcRegionArea('cytosol_ec_membrane')",
                      variable_type=VariableType.MEMBRANE_REGION),
        NamedFunction(name="cytosol_ec_membrane::sobj_cytosol1_ec0_size",
                      vcell_expression = "vcRegionArea('cytosol_ec_membrane')",
                      variable_type=VariableType.MEMBRANE_REGION),
        NamedFunction(name="Nucleus_cytosol_membrane::sobj_Nucleus2_cytosol1_size",
                      vcell_expression = "vcRegionArea('Nucleus_cytosol_membrane')",
                      variable_type=VariableType.MEMBRANE_REGION),
        NamedFunction(name="cytosol::vobj_cytosol1_size",
                      vcell_expression = "vcRegionVolume('cytosol')",
                      variable_type=VariableType.VOLUME_REGION),
        NamedFunction(name="ec::vobj_ec0_size",
                      vcell_expression="vcRegionVolume('ec')",
                      variable_type=VariableType.VOLUME_REGION),
        NamedFunction(name="Nucleus::vobj_Nucleus2_size",
                      vcell_expression="vcRegionVolume('Nucleus')",
                      variable_type=VariableType.VOLUME_REGION)
    ]
    assert [nf.name for nf in data_functions.named_functions] == [nf.name for nf in expected_functions]
    assert [nf.vcell_expression for nf in data_functions.named_functions] == [nf.vcell_expression for nf in expected_functions]
    assert [nf.variable_type for nf in data_functions.named_functions] == [nf.variable_type for nf in expected_functions]


def test_function_eval():
    extract_simdata()

    pde_dataset = PdeDataSet(base_dir=test_data_dir, log_filename="SimID_946368938_0_.log")
    pde_dataset.read()
    data_functions = DataFunctions(function_file=test_data_dir / "SimID_946368938_0_.functions")
    data_functions.read()

    volume_functions: list[NamedFunction] = [nf for nf in data_functions.named_functions if nf.variable_type == VariableType.VOLUME]
    assert len(volume_functions) == 1
    function_J_r0 = volume_functions[0]
    assert function_J_r0.name == "cytosol::J_r0"
    assert function_J_r0.name.split("::")[1] == "J_r0"

    assert function_J_r0.variables == ['RanC_cyt', 'Ran_cyt', 'C_cyt']
    assert function_J_r0.python_expression == "(RanC_cyt - (1000.0 * C_cyt * Ran_cyt))"
    min_values = []
    max_values = []
    for t in pde_dataset.times():
        # for "RanC_cyt - (1000.0 * C_cyt * Ran_cyt)"
        RanC_cyt: np.ndarray = pde_dataset.get_data("cytosol::RanC_cyt", t)
        C_cyt: np.ndarray = pde_dataset.get_data("cytosol::C_cyt", t)
        Ran_cyt: np.ndarray = pde_dataset.get_data("cytosol::Ran_cyt", t)
        bindings = {"RanC_cyt": RanC_cyt, "C_cyt": C_cyt, "Ran_cyt": Ran_cyt}
        J_r0: np.ndarray = function_J_r0.evaluate(bindings)

        assert J_r0.shape == (126025,)
        assert np.allclose(J_r0, RanC_cyt - (1000.0 * C_cyt * Ran_cyt))

        min_values.append(np.min(J_r0))
        max_values.append(np.max(J_r0))

    assert min_values == [0.0, 0.0, 0.0, 0.0, 0.0]
    assert max_values == [0.0, 0.00013838468860665845, 0.0001493452037574851, 0.0001484768688158302, 0.00014236316719653776]


def test_mesh_parse():
    extract_simdata()

    mesh = CartesianMesh(mesh_file=test_data_dir / "SimID_946368938_0_.mesh")
    mesh.read()


def test_post_processing_parse():
    extract_simdata()

    post_processing = PostProcessing(postprocessing_hdf5_path=test_data_dir / "SimID_946368938_0_.hdf5")
    post_processing.read()

    expected_times = [0.0, 0.25, 0.5, 0.75, 1.0]
    assert np.allclose(post_processing.times, expected_times)

    expected_variables = [
        VariableInfo(stat_var_name="C_cyt_average", stat_var_unit="uM", stat_channel=0, var_index=0),
        VariableInfo(stat_var_name="C_cyt_total", stat_var_unit="molecules", stat_channel=1, var_index=0),
        VariableInfo(stat_var_name="C_cyt_min", stat_var_unit="uM", stat_channel=2, var_index=0),
        VariableInfo(stat_var_name="C_cyt_max", stat_var_unit="uM", stat_channel=3, var_index=0),
        VariableInfo(stat_var_name="Ran_cyt_average", stat_var_unit="uM", stat_channel=4, var_index=1),
        VariableInfo(stat_var_name="Ran_cyt_total", stat_var_unit="molecules", stat_channel=5, var_index=1),
        VariableInfo(stat_var_name="Ran_cyt_min", stat_var_unit="uM", stat_channel=6, var_index=1),
        VariableInfo(stat_var_name="Ran_cyt_max", stat_var_unit="uM", stat_channel=7, var_index=1),
        VariableInfo(stat_var_name="RanC_cyt_average", stat_var_unit="uM", stat_channel=8, var_index=2),
        VariableInfo(stat_var_name="RanC_cyt_total", stat_var_unit="molecules", stat_channel=9, var_index=2),
        VariableInfo(stat_var_name="RanC_cyt_min", stat_var_unit="uM", stat_channel=10, var_index=2),
        VariableInfo(stat_var_name="RanC_cyt_max", stat_var_unit="uM", stat_channel=11, var_index=2),
        VariableInfo(stat_var_name="RanC_nuc_average", stat_var_unit="uM", stat_channel=12, var_index=3),
        VariableInfo(stat_var_name="RanC_nuc_total", stat_var_unit="molecules", stat_channel=13, var_index=3),
        VariableInfo(stat_var_name="RanC_nuc_min", stat_var_unit="uM", stat_channel=14, var_index=3),
        VariableInfo(stat_var_name="RanC_nuc_max", stat_var_unit="uM", stat_channel=15, var_index=3)
    ]
    # compare each field of the VariableInfo objects
    for i, v in enumerate(post_processing.variables):
        expected = expected_variables[i]
        assert v.stat_var_name == expected.stat_var_name
        assert v.stat_var_unit == expected.stat_var_unit
        assert v.stat_channel == expected.stat_channel
        assert v.var_index == expected.var_index

    stats_table_from_vcell_plot = """
    t	C_cyt_average	C_cyt_total	C_cyt_min	C_cyt_max	Ran_cyt_average	Ran_cyt_total	Ran_cyt_min	Ran_cyt_max	RanC_cyt_average	RanC_cyt_total	RanC_cyt_min	RanC_cyt_max	RanC_nuc_average	RanC_nuc_total	RanC_nuc_min	RanC_nuc_max
    0.0	0.0	0.0	0.0	2.2250738585072014E-308	0.0	0.0	0.0	2.2250738585072014E-308	0.0	0.0	0.0	2.2250738585072014E-308	4.500000000000469E-4	995.2639514331112	4.5E-4	4.5E-4
    0.25	1.7242715861760507E-6	15.424394116394046	0.0	1.6578610937269188E-5	1.7242715861760507E-6	15.424394116394046	0.0	1.6578610937269188E-5	1.207791769681895E-5	108.04247089303067	0.0	1.386595389472678E-4	3.941755233129602E-4	871.7970864237174	2.618557008421516E-4	4.4784349464175205E-4
    0.5	5.558151571952027E-6	49.720195525909354	0.0	3.688810690038264E-5	5.558151571952027E-6	49.720195525909354	0.0	3.688810690038264E-5	1.8059551583630324E-5	161.55090846739807	0.0	1.5070593618817915E-4	3.5447559498153353E-4	783.992847439835	2.1160422746379327E-4	4.325308047580796E-4
    0.75	1.047028801407123E-5	93.66149169073127	0.0	5.838639163921412E-5	1.047028801407123E-5	93.66149169073127	0.0	5.838639163921412E-5	2.1237498474550513E-5	189.9790898046723	0.0	1.518858395444779E-4	3.217543607511082E-4	711.6233699377424	1.8374881412946774E-4	4.084354763369491E-4
    1.0	1.5889249586954565E-5	142.1365693245928	0.0	7.973304853048764E-5	1.5889249586954565E-5	142.1365693245928	0.0	7.973304853048764E-5	2.277124484748409E-5	203.69914917373157	0.0	1.4872052622450286E-4	2.9363336670624725E-4	649.4282329348212	1.6516455078631075E-4	3.8171626169331387E-4
    """
    # parse table from vcell plot into a np.ndarray
    stats_lines = stats_table_from_vcell_plot.strip().split("\n")
    stats_data = []
    for line in stats_lines[1:]:
        parts = line.split("\t")
        stats_data.append([float(v) for v in parts])
    stats_data = np.array(stats_data)

    # remove first column of times and reshape to look like the post_processing.statistics array
    stats_data = stats_data[:, 1:]
    stats_data = stats_data.reshape((5, 4, 4))

    assert np.allclose(post_processing.statistics, stats_data)

    assert post_processing.statistics.shape == (5, 4, 4)

    # compare individual statistics for C_cyt (first variable): average, total, min, max to known values
    expected_C_cyt_average = np.array([[0.00000000e+00, 1.72427159e-06, 5.55815157e-06, 1.04702880e-05, 1.58892496e-05]])
    C_cyt_average = post_processing.statistics[:, 0, int(StatisticType.AVERAGE)]
    assert np.allclose(C_cyt_average, expected_C_cyt_average)

    expected_C_cyt_total = np.array([[[[  0., 15.42439412,  49.72019553,  93.66149169, 142.13656932]]]])
    C_cyt_total = post_processing.statistics[:, 0, int(StatisticType.TOTAL)]
    assert np.allclose(C_cyt_total, expected_C_cyt_total)

    expected_C_cyt_min = np.array([[0., 0., 0., 0., 0.]])
    C_cyt_min = post_processing.statistics[:, 0, int(StatisticType.MIN)]
    assert np.allclose(C_cyt_min, expected_C_cyt_min)

    expected_C_cyt_max = np.array([[[2.22507386e-308, 1.65786109e-005, 3.68881069e-005, 5.83863916e-005, 7.97330485e-005]]])
    C_cyt_max = post_processing.statistics[:, 0, int(StatisticType.MAX)]
    assert np.allclose(C_cyt_max, expected_C_cyt_max)

    fluorescence: ImageMetadata = post_processing.image_metadata[0]
    assert fluorescence.name == "fluor"
    assert fluorescence.shape == (71, 71)
    assert np.allclose(fluorescence.extents, np.array([74.24, 74.24, 26.0]))
    assert np.allclose(fluorescence.origin, np.array([0.0, 0.0, 0.0]))
    assert fluorescence.group_path == "/PostProcessing/fluor"

    fluorescence_data_0 = post_processing.read_image_data(image_metadata=fluorescence, time_index=0)
    assert fluorescence_data_0.shape == (71, 71)
    assert fluorescence_data_0.dtype == np.float64
    assert np.min(fluorescence_data_0) == 0.0
    assert np.max(fluorescence_data_0) == 0.0

    fluorescence_data_4 = post_processing.read_image_data(image_metadata=fluorescence, time_index=4)
    assert fluorescence_data_4.shape == (71, 71)
    assert fluorescence_data_4.dtype == np.float64
    assert np.min(fluorescence_data_4) == 0.0
    assert np.allclose(np.max(fluorescence_data_4), 0.7147863306841433)

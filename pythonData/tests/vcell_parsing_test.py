import numpy as np
from pathlib import Path
import tarfile

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
    print("done")


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

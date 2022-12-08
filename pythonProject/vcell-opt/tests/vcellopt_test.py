import json
from pathlib import Path

import basico
import pandas as pd

from vcell_opt.data import OptProblem
from vcell_opt.optUtils import get_reference_data, get_fit_parameters, get_copasi_opt_method_settings


def test_read_opt_problem() -> None:
    opt_file = Path(__file__).parent.parent / "test_data" / "optproblem.json"
    with open(opt_file, "r") as f_optfile:
        vcell_opt_problem: OptProblem = OptProblem.from_json_data(json.load(f_optfile))


def test_run() -> None:
    opt_file = Path(__file__).parent.parent / "test_data" / "optproblem.json"
    with open(opt_file, "r") as f_optfile:
        vcell_opt_problem: OptProblem = OptProblem.from_json_data(json.load(f_optfile))

    copasi_model = basico.load_model(vcell_opt_problem.math_model_sbml_contents)

    exp_data = get_reference_data(vcell_opt_problem)
    basico.add_experiment('exp1', data=exp_data)

    task_settings = basico.get_task_settings('Parameter Estimation')
    task_settings['method'] = get_copasi_opt_method_settings(vcell_opt_problem)
    basico.set_task_settings('Parameter Estimation', task_settings)

    fit_items = get_fit_parameters(vcell_opt_problem)
    basico.set_fit_parameters(fit_items)

    results: pd.DataFrame = basico.run_parameter_estimation(update_model=True)

    assert results is not None

    fit_solution: pd.DataFrame = basico.task_parameterestimation.get_parameters_solution()
    print(fit_solution)
    fit_Kf = fit_solution.loc['Values[Kf]']['sol']
    fit_Kr = fit_solution.loc['Values[Kr]']['sol']
    fit_s0_init_uM = fit_solution.loc['Values[s0_init_uM]']['sol']

    expected_fit_Kf = 0.812494
    expected_fit_Kr = 0.687506
    expected_fit_s0_init_uM = 0.000031

    assert abs(fit_Kr - expected_fit_Kr) < 1e-5
    assert abs(fit_Kf - expected_fit_Kf) < 1e-5
    assert abs(fit_s0_init_uM - expected_fit_s0_init_uM) < 1e-5

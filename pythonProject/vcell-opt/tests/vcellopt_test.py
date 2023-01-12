import json
import os
import tempfile
from pathlib import Path
from typing import Dict

import basico
import pandas as pd

from vcell_opt.data import OptProblem
from vcell_opt.optUtils import get_reference_data, get_fit_parameters, get_copasi_opt_method_settings


def test_read_opt_problem() -> None:
    opt_file = Path(__file__).parent.parent / "test_data" / "optproblem.json"
    with open(opt_file, "r") as f_optfile:
        vcell_opt_problem: OptProblem = OptProblem.from_json_data(json.load(f_optfile))


def test_run() -> None:
    report_file: Path = Path(__file__).parent.parent / "test_data" / "optproblem.report"
    if report_file.exists():
        os.remove(report_file)

    opt_file = Path(__file__).parent.parent / "test_data" / "optproblem.json"
    with open(opt_file, "r") as f_optfile:
        vcell_opt_problem: OptProblem = OptProblem.from_json_data(json.load(f_optfile))

    os.chdir(tempfile.gettempdir())

    copasi_model = basico.load_model(vcell_opt_problem.math_model_sbml_contents)

    exp_data = get_reference_data(vcell_opt_problem)
    basico.add_experiment('exp1', data=exp_data)

    task_settings = basico.get_task_settings('Parameter Estimation')
    task_settings['method'] = get_copasi_opt_method_settings(vcell_opt_problem)
    basico.set_task_settings('Parameter Estimation', task_settings)

    #
    # define parameter estimation report format, note that header and footer are omitted to ease parsing
    #
    basico.add_report('parest report', task=basico.T.PARAMETER_ESTIMATION,
                      body=['CN=Root,Vector=TaskList[Parameter Estimation],Problem=Parameter Estimation,Reference=Function Evaluations',
                            '\\\t',
                            'CN=Root,Vector=TaskList[Parameter Estimation],Problem=Parameter Estimation,Reference=Best Value',
                            '\\\t',
                            'CN=Root,Vector=TaskList[Parameter Estimation],Problem=Parameter Estimation,Reference=Best Parameters'
                            ],
                      )
    basico.assign_report("parest report", task=basico.T.PARAMETER_ESTIMATION, filename=str(report_file), append=True)

    fit_items = get_fit_parameters(vcell_opt_problem)
    basico.set_fit_parameters(fit_items)

    results: pd.DataFrame = basico.run_parameter_estimation(update_model=True)

    assert results is not None

    fit_solution: pd.DataFrame = basico.task_parameterestimation.get_parameters_solution()
    print(fit_solution)
    fit_Kf = fit_solution.loc['Values[Kf]']['sol']
    fit_Kr = fit_solution.loc['Values[Kr]']['sol']
    fit_s0_init_uM = fit_solution.loc['Values[s0_init_uM]']['sol']

    # read last line in report and see that the parameter values match
    with open(report_file, "r") as f_reportfile:
        for line in f_reportfile:
            pass
        last_line = line

    #
    # verify that last line of report matches returned best fit
    #
    report_tokens = last_line.split("\t")
    assert abs(float(report_tokens[3]) - fit_Kf) < 1e-5
    assert abs(float(report_tokens[4]) - fit_Kr) < 1e-5
    assert abs(float(report_tokens[5]) - fit_s0_init_uM) < 1e-5

    # remove report file
    if report_file.exists():
        os.remove(report_file)

    expected_fit_Kf = 0.812494
    expected_fit_Kr = 0.687506
    expected_fit_s0_init_uM = 0.000031

    #
    # using loose tolerances here because this test uses a stochastic method (even with fixed seed)
    # TODO: the test model should use a local gradient-based method whose solution is determinstic.
    #
    assert abs(fit_Kr - expected_fit_Kr) < 1e-4
    assert abs(fit_Kf - expected_fit_Kf) < 1e-4
    assert abs(fit_s0_init_uM - expected_fit_s0_init_uM) < 1e-4

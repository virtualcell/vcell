import json
from pathlib import Path

import basico
import pandas as pd
import typer
from typing import Dict

from vcell_opt.data import OptProblem, Vcellopt, VcelloptStatus, OptResultSet
from vcell_opt.optUtils import get_reference_data, get_fit_parameters, get_copasi_opt_method_settings, \
    result_set_from_fit


def run_command(opt_file: Path = typer.Argument(..., file_okay=True, dir_okay=False, exists=True,
                                                help="optimization input json file"),
                result_file: Path = typer.Argument(..., file_okay=True, dir_okay=False,
                                                   help="optimization result output json file")):
    if opt_file is None or result_file is None:
        print("use --help for help")
        return typer.Exit(-1)

    with open(opt_file, "rb") as f_optfile:
        opt_file_json = json.load(f_optfile)
        opt_problem: OptProblem = OptProblem.from_json_data(opt_file_json)

    basico.load_model_from_string(opt_problem.math_model_sbml_contents)

    exp_data = get_reference_data(opt_problem)
    basico.add_experiment('exp1', data=exp_data)

    task_settings = basico.get_task_settings('Parameter Estimation')
    task_settings['method'] = get_copasi_opt_method_settings(opt_problem)
    basico.set_task_settings('Parameter Estimation', task_settings)

    fit_items = get_fit_parameters(opt_problem)
    basico.set_fit_parameters(fit_items)

    results: pd.DataFrame = basico.run_parameter_estimation(update_model=True)
    assert results is not None

    fit_solution: pd.DataFrame = basico.task_parameterestimation.get_parameters_solution()

    opt_parameter_values: Dict[str, float] = result_set_from_fit(fit_solution)
    fit_statistics: Dict[str, float] = basico.task_parameterestimation.get_fit_statistic(include_parameters=True,
                                                                                         include_experiments=True,
                                                                                         include_fitted=True)
    objective_function = fit_statistics['obj']
    num_function_evaluations = fit_statistics['f_evals']

    result_set = OptResultSet(num_function_evaluations=int(num_function_evaluations),
                              objective_function=objective_function, opt_parameter_values=opt_parameter_values)

    opt_result_set: Dict[str, float] = result_set_from_fit(fit_solution)

    status = VcelloptStatus.COMPLETE
    status_message = str(basico.task_parameterestimation.get_fit_statistic(
        include_parameters=True, include_experiments=True, include_fitted=True))

    opt_run: Vcellopt = Vcellopt(opt_problem=opt_problem, opt_result_set=result_set, status=VcelloptStatus.COMPLETE,
                                 status_message=status_message)
    with open(result_file, "w") as f_result_file:
        json_data = opt_run.to_json_data()
        json.dump(json_data, f_result_file)


if __name__ == "__main__":
    typer.run(run_command)

import json
import multiprocessing
import os
import tempfile
import time
from pathlib import Path
from typing import List

import basico
import pandas as pd

from vcell_opt.data import OptProblem, Vcellopt
from vcell_opt.optUtils import get_reference_data, get_fit_parameters, get_copasi_opt_method_settings, get_progress_report
import vcell_opt.optService as optService


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
                      separator='\t',
                      body=['CN=Root,Vector=TaskList[Parameter Estimation],Problem=Parameter Estimation,Reference=Function Evaluations',
                            'CN=Root,Vector=TaskList[Parameter Estimation],Problem=Parameter Estimation,Reference=Best Value',
                            'CN=Root,Vector=TaskList[Parameter Estimation],Problem=Parameter Estimation,Reference=Best Parameters'
                            ],
                      )

    # write the header (easier to do it here than to ask COPASI to do it)
    with open(report_file, 'w') as f_report_file:
        param_names: List[str] = [param_desc.name for param_desc in vcell_opt_problem.parameter_description_list]
        f_report_file.write(json.dumps(param_names)+"\n")

    basico.assign_report("parest report", task=basico.T.PARAMETER_ESTIMATION, filename=str(report_file), append=True,
                         confirm_overwrite=False)

    fit_items = get_fit_parameters(vcell_opt_problem)
    basico.set_fit_parameters(fit_items)

    results: pd.DataFrame = basico.run_parameter_estimation(update_model=True)

    assert results is not None

    fit_solution: pd.DataFrame = basico.task_parameterestimation.get_parameters_solution()
    print(fit_solution)
    fit_Kf = fit_solution.loc['Values[Kf]']['sol']
    fit_Kr = fit_solution.loc['Values[Kr]']['sol']
    fit_s0_init_uM = fit_solution.loc['Values[s0_init_uM]']['sol']

    # read last line in report and see that the parameter values match (expected first and last lines as follows)
    #
    # 20	0.559754	(	1.2751	0.73804	7.07081e-06	)
    # ...
    # 3480	4.97871e-14	(	0.812494	0.687506	3.10832e-05	)
    #

    with open(report_file, "r") as f_reportfile:
        for line in f_reportfile:
            pass
        last_line = line

    #
    # verify that last line of report matches returned best fit
    #
    report_tokens = last_line.split("\t")
    last_num_evaluations = int(report_tokens[0])
    last_objective_function_report = float(report_tokens[1])
    assert abs(float(report_tokens[3]) - fit_Kf) < 1e-5
    assert abs(float(report_tokens[4]) - fit_Kr) < 1e-5
    assert abs(float(report_tokens[5]) - fit_s0_init_uM) < 1e-5

    #
    # verify that last line of progress report object matches best fit
    #
    progress_report = get_progress_report(report_file=report_file)
    first_progress_item = progress_report.progress_items[0]
    last_progress_item = progress_report.progress_items[len(progress_report.progress_items)-1]
    assert first_progress_item.num_function_evaluations == 20
    assert first_progress_item.obj_func_value == 0.559754
    assert last_progress_item.num_function_evaluations == last_num_evaluations
    assert last_progress_item.obj_func_value == last_objective_function_report
    assert abs(progress_report.best_param_values["Kf"] - fit_Kf) < 1e-5
    assert abs(progress_report.best_param_values["Kr"] - fit_Kr) < 1e-5
    assert abs(progress_report.best_param_values["s0_init_uM"] - fit_s0_init_uM) < 1e-5

    # remove report file
    # if report_file.exists():
    #     os.remove(report_file)

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


def test_solver() -> None:
    opt_file = Path(__file__).parent.parent / "test_data" / "optproblem.json"

    report_file: Path = Path(__file__).parent.parent / "test_data" / "service_optproblem.report"
    result_file = Path(__file__).parent.parent / "test_data" / "service_optresults.json"

    optService.run_command(opt_file=opt_file, report_file=report_file, result_file=result_file)


    with open(opt_file, "r") as f_optfile:
        vcell_opt_problem: OptProblem = OptProblem.from_json_data(json.load(f_optfile))

    with open(result_file, "r") as f_resultfile:
        opt_run: Vcellopt = Vcellopt.from_json_data(json.load(f_resultfile))

    assert opt_run is not None

    fit_Kf = opt_run.opt_result_set.opt_parameter_values["Kf"]
    fit_Kr = opt_run.opt_result_set.opt_parameter_values["Kr"]
    fit_s0_init_uM = opt_run.opt_result_set.opt_parameter_values["s0_init_uM"]

    # read last line in report and see that the parameter values match (expected first and last lines as follows)
    #
    # 20	0.559754	(	1.2751	0.73804	7.07081e-06	)
    # ...
    # 3480	4.97871e-14	(	0.812494	0.687506	3.10832e-05	)
    #

    with open(report_file, "r") as f_reportfile:
        for line in f_reportfile:
            pass
        last_line = line

    #
    # verify that last line of report matches returned best fit
    #
    report_tokens = last_line.split("\t")
    last_num_evaluations = int(report_tokens[0])
    last_objective_function_report = float(report_tokens[1])
    assert abs(float(report_tokens[3]) - fit_Kf) < 1e-5
    assert abs(float(report_tokens[4]) - fit_Kr) < 1e-5
    assert abs(float(report_tokens[5]) - fit_s0_init_uM) < 1e-5

    #
    # verify that last line of progress report object matches best fit
    #
    progress_report = get_progress_report(report_file=report_file)
    first_progress_item = progress_report.progress_items[0]
    last_progress_item = progress_report.progress_items[len(progress_report.progress_items)-1]
    assert first_progress_item.num_function_evaluations == 20
    assert first_progress_item.obj_func_value == 0.559754
    assert last_progress_item.num_function_evaluations == last_num_evaluations
    assert last_progress_item.obj_func_value == last_objective_function_report
    assert abs(progress_report.best_param_values["Kf"] - fit_Kf) < 1e-5
    assert abs(progress_report.best_param_values["Kr"] - fit_Kr) < 1e-5
    assert abs(progress_report.best_param_values["s0_init_uM"] - fit_s0_init_uM) < 1e-5

    # remove report file
    # if report_file.exists():
    #     os.remove(report_file)

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

    if report_file.exists():
        os.remove(report_file)

    if result_file.exists():
        os.remove(result_file)


def _run_solver_in_subprocess(opt_file: str, report_file: str, result_file: str) -> None:
    """Target function for multiprocessing — runs the solver in a separate process."""
    optService.run_command(opt_file=Path(opt_file), report_file=Path(report_file), result_file=Path(result_file))


def test_incremental_report_writing() -> None:
    """Verify that the report file is written incrementally during optimization,
    not buffered until completion. This requires confirm_overwrite=False in
    basico.assign_report() (the default True causes COPASI to buffer all output).

    Uses multiprocessing so the solver runs in a separate process (with its own GIL),
    allowing the main process to monitor the report file size during execution.
    """
    test_data_dir = Path(__file__).resolve().parent.parent / "test_data"
    opt_file = test_data_dir / "optproblem.json"
    report_file = test_data_dir / "incremental_test.report"
    result_file = test_data_dir / "incremental_test.json"

    if report_file.exists():
        os.remove(report_file)
    if result_file.exists():
        os.remove(result_file)

    # Run the solver in a separate process
    proc = multiprocessing.Process(
        target=_run_solver_in_subprocess,
        args=(str(opt_file), str(report_file), str(result_file))
    )
    proc.start()

    # Monitor the report file size from the main process
    file_sizes: List[int] = []
    while proc.is_alive():
        if report_file.exists():
            file_sizes.append(report_file.stat().st_size)
        time.sleep(0.01)  # 10ms polling

    proc.join(timeout=30)
    assert proc.exitcode == 0, f"Solver process failed with exit code {proc.exitcode}"

    unique_sizes = sorted(set(file_sizes))
    print(f"Sampled {len(file_sizes)} times, unique file sizes: {unique_sizes}")

    # With confirm_overwrite=False, COPASI flushes progress incrementally.
    # We expect at least 3 distinct sizes: header-only, intermediate progress, final.
    # With confirm_overwrite=True (old behavior), we'd see only the header size
    # until the solver finishes, then a single jump to the final size (2 unique sizes).
    assert len(unique_sizes) >= 3, (
        f"Expected at least 3 distinct file sizes (header, intermediate, final) "
        f"but got {len(unique_sizes)}: {unique_sizes}. "
        f"COPASI may be buffering output — check confirm_overwrite setting."
    )

    # Cleanup
    if report_file.exists():
        os.remove(report_file)
    if result_file.exists():
        os.remove(result_file)

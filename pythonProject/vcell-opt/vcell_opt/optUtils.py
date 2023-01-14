from pathlib import Path
from typing import Dict, List, Union

import basico
import pandas as pd

from .data import CopasiOptimizationMethodOptimizationMethodType, CopasiOptimizationParameterParamType, OptProblem, \
    OptProgressItem, OptProgressReport


def get_copasi_opt_method_settings(vcell_opt_problem: OptProblem) -> Dict[str, Union[str, float]]:
    method: str = _get_copasi_opt_method(vcell_opt_problem.copasi_optimization_method.optimization_method_type)
    settings: Dict[str, Union[str, float]] = dict(name=method)
    for method_param in vcell_opt_problem.copasi_optimization_method.optimization_parameter:
        settings[_get_copasi_method_param(method_param.param_type)] = method_param.value
    return settings


def result_set_from_fit(fit_solution: pd.DataFrame) -> Dict[str, float]:
    return {name.replace('Values[', '').replace(']', ''): value for name, value in
     zip(fit_solution.index, fit_solution['sol'])}


def _get_copasi_method_param(param_type: CopasiOptimizationParameterParamType) -> str:
    if param_type == CopasiOptimizationParameterParamType.NUMBER_OF_GENERATIONS:
        return "Number of Generations"
    if param_type == CopasiOptimizationParameterParamType.PF:
        return "Pf"
    if param_type == CopasiOptimizationParameterParamType.COOLING_FACTOR:
        return "Cooling Factor"
    if param_type == CopasiOptimizationParameterParamType.ITERATION_LIMIT:
        return "Iteration Limit"
    if param_type == CopasiOptimizationParameterParamType.NUMBER_OF_ITERATIONS:
        return "Number of Iterations"
    if param_type == CopasiOptimizationParameterParamType.POPULATION_SIZE:
        return "Population Size"
    if param_type == CopasiOptimizationParameterParamType.RANDOM_NUMBER_GENERATOR:
        return "Random Number Generator"
    if param_type == CopasiOptimizationParameterParamType.RHO:
        return "Rho"
    if param_type == CopasiOptimizationParameterParamType.SCALE:
        return "Scale"
    if param_type == CopasiOptimizationParameterParamType.SEED:
        return "Seed"
    if param_type == CopasiOptimizationParameterParamType.START_TEMPERATURE:
        return "Start Temperature"
    if param_type == CopasiOptimizationParameterParamType.TOLERANCE:
        return "Tolerance"
    raise Exception(f"unexpected parameter type {param_type}")


def _get_copasi_opt_method(vcell_opt_type: CopasiOptimizationMethodOptimizationMethodType) -> str:
    if vcell_opt_type == CopasiOptimizationMethodOptimizationMethodType.SRES:
        return basico.PE.EVOLUTIONARY_STRATEGY_SRES
    if vcell_opt_type == CopasiOptimizationMethodOptimizationMethodType.PRAXIS:
        return basico.PE.PRAXIS
    if vcell_opt_type == CopasiOptimizationMethodOptimizationMethodType.NELDER_MEAD:
        return basico.PE.NELDER_MEAD
    if vcell_opt_type == CopasiOptimizationMethodOptimizationMethodType.HOOKE_JEEVES:
        return basico.PE.HOOKE_JEEVES
    if vcell_opt_type == CopasiOptimizationMethodOptimizationMethodType.EVOLUTIONARY_PROGRAM:
        return basico.PE.EVOLUTIONARY_PROGRAMMING
    if vcell_opt_type == CopasiOptimizationMethodOptimizationMethodType.GENETIC_ALGORITHM:
        return basico.PE.GENETIC_ALGORITHM
    if vcell_opt_type == CopasiOptimizationMethodOptimizationMethodType.GENETIC_ALGORITHM_SR:
        return basico.PE.GENETIC_ALGORITHM_SR
    if vcell_opt_type == CopasiOptimizationMethodOptimizationMethodType.LEVENBERG_MARQUARDT:
        return basico.PE.LEVENBERG_MARQUARDT
    if vcell_opt_type == CopasiOptimizationMethodOptimizationMethodType.PARTICLE_SWARM:
        return basico.PE.PARTICLE_SWARM
    if vcell_opt_type == CopasiOptimizationMethodOptimizationMethodType.RANDOM_SEARCH:
        return basico.PE.RANDOM_SEARCH
    if vcell_opt_type == CopasiOptimizationMethodOptimizationMethodType.SIMULATED_ANNEALING:
        return basico.PE.SIMULATED_ANNEALING
    if vcell_opt_type == CopasiOptimizationMethodOptimizationMethodType.STEEPEST_DESCENT:
        return basico.PE.STEEPEST_DESCENT
    if vcell_opt_type == CopasiOptimizationMethodOptimizationMethodType.TRUNCATED_NEWTON:
        return basico.PE.TRUNCATED_NEWTON
    raise Exception(f"unexpected optimization type {vcell_opt_type}")


def _fix_refvar_name(name: str) -> str:
    if name == 't':
        return 'Time'
    return f"Values[{name}]"


def get_reference_data(vcell_opt_problem: OptProblem) -> pd.DataFrame:
    columns = [_fix_refvar_name(ref_var.var_name) for ref_var in vcell_opt_problem.reference_variable]
    df = pd.DataFrame(vcell_opt_problem.data_set, columns=columns)
    return df


def get_fit_parameters(vcell_opt_problem: OptProblem) -> List[Dict[str, Union[str, float]]]:
    fit_items: List[Dict[str, Union[str, float]]] = [dict(name=f"Values[{a.name}]", lower=a.min_value, upper=a.max_value) for a in vcell_opt_problem.parameter_description_list]
    return fit_items

def get_progress_report(report_file: Path) -> OptProgressReport:
    '''
    each line in file is as follows (tab separated)
    100  0.000233223 ( 3.332 5.433 6.543 )
    '''
    progress_items: List[OptProgressItem] = []
    with open(report_file, "r") as f_reportfile:
        for line in f_reportfile:
            tokens = line.split("\t")
            iteration = int(tokens[0])
            objective_function = float(tokens[1])
            param_values = [float(token) for token in tokens[3:len(tokens)-1]]
            progress_item = OptProgressItem(
                iteration=iteration,
                obj_func_value=objective_function,
                best_param_values=param_values)
            progress_items.append(progress_item)
    return OptProgressReport(progress_items=progress_items)

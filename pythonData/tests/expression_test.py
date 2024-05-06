import numpy as np

from vcelldata.simdata_models import NamedFunction, VariableType


def test_namedfunction_eval_add():
    a = np.array([1, 2, 3])
    b = np.array([4, 5, 6])
    c = np.array([7, 8, 9])
    vcell_expression = "v0 + v1 + v2"
    bindings = {"v0": a, "v1": b, "v2": c}

    function = NamedFunction(name="func1", vcell_expression=vcell_expression, variable_type=VariableType.VOLUME)
    d: np.ndarray = function.evaluate(variable_bindings=bindings)

    assert np.array_equal(d, a + b + c)
    assert np.array_equal(d, np.array([12, 15, 18]))
    assert np.array_equal(d, np.array([12.0, 15.0, 18.0]))


def test_namedfunction_eval_power():
    a = np.array([1, 2, 3])
    b = np.array([4, 5, 6])
    c = np.array([7, 8, 9])
    vcell_expression = "v0 + v1^v2"
    bindings = {"v0": a, "v1": b, "v2": c}

    function = NamedFunction(name="func1", vcell_expression=vcell_expression, variable_type=VariableType.VOLUME)
    d: np.ndarray = function.evaluate(variable_bindings=bindings)

    assert np.array_equal(d, a + b**c)
    assert np.array_equal(d, np.array([a[0] + b[0]**c[0],
                                       a[1] + b[1]**c[1],
                                       a[2] + b[2]**c[2]]))
    assert np.array_equal(d, np.array([16385, 390627, 10077699]))

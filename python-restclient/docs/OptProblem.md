# OptProblem


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**copasi_optimization_method** | [**CopasiOptimizationMethod**](CopasiOptimizationMethod.md) |  | [optional] 
**data_set** | **List[List[float]]** |  | [optional] 
**math_model_sbml_contents** | **str** |  | [optional] 
**number_of_optimization_runs** | **int** |  | [optional] 
**parameter_description_list** | [**List[ParameterDescription]**](ParameterDescription.md) |  | [optional] 
**reference_variable** | [**List[ReferenceVariable]**](ReferenceVariable.md) |  | [optional] 

## Example

```python
from vcell_client.models.opt_problem import OptProblem

# TODO update the JSON string below
json = "{}"
# create an instance of OptProblem from a JSON string
opt_problem_instance = OptProblem.from_json(json)
# print the JSON string representation of the object
print OptProblem.to_json()

# convert the object into a dict
opt_problem_dict = opt_problem_instance.to_dict()
# create an instance of OptProblem from a dict
opt_problem_form_dict = opt_problem.from_dict(opt_problem_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



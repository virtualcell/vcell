# CopasiOptimizationParameter


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**data_type** | [**CopasiOptimizationParameterDataType**](CopasiOptimizationParameterDataType.md) |  | [optional] 
**param_type** | [**CopasiOptimizationParameterParamType**](CopasiOptimizationParameterParamType.md) |  | [optional] 
**value** | **float** |  | [optional] 

## Example

```python
from vcell_client.models.copasi_optimization_parameter import CopasiOptimizationParameter

# TODO update the JSON string below
json = "{}"
# create an instance of CopasiOptimizationParameter from a JSON string
copasi_optimization_parameter_instance = CopasiOptimizationParameter.from_json(json)
# print the JSON string representation of the object
print CopasiOptimizationParameter.to_json()

# convert the object into a dict
copasi_optimization_parameter_dict = copasi_optimization_parameter_instance.to_dict()
# create an instance of CopasiOptimizationParameter from a dict
copasi_optimization_parameter_form_dict = copasi_optimization_parameter.from_dict(copasi_optimization_parameter_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



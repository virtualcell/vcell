# CopasiOptimizationMethod


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**optimization_method_type** | [**CopasiOptimizationMethodOptimizationMethodType**](CopasiOptimizationMethodOptimizationMethodType.md) |  | [optional] 
**optimization_parameter** | [**List[CopasiOptimizationParameter]**](CopasiOptimizationParameter.md) |  | [optional] 

## Example

```python
from vcell_client.models.copasi_optimization_method import CopasiOptimizationMethod

# TODO update the JSON string below
json = "{}"
# create an instance of CopasiOptimizationMethod from a JSON string
copasi_optimization_method_instance = CopasiOptimizationMethod.from_json(json)
# print the JSON string representation of the object
print CopasiOptimizationMethod.to_json()

# convert the object into a dict
copasi_optimization_method_dict = copasi_optimization_method_instance.to_dict()
# create an instance of CopasiOptimizationMethod from a dict
copasi_optimization_method_form_dict = copasi_optimization_method.from_dict(copasi_optimization_method_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



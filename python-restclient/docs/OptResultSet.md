# OptResultSet


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**num_function_evaluations** | **int** |  | [optional] 
**objective_function** | **float** |  | [optional] 
**opt_parameter_values** | **Dict[str, float]** |  | [optional] 
**opt_progress_report** | [**OptProgressReport**](OptProgressReport.md) |  | [optional] 

## Example

```python
from vcell_client.models.opt_result_set import OptResultSet

# TODO update the JSON string below
json = "{}"
# create an instance of OptResultSet from a JSON string
opt_result_set_instance = OptResultSet.from_json(json)
# print the JSON string representation of the object
print OptResultSet.to_json()

# convert the object into a dict
opt_result_set_dict = opt_result_set_instance.to_dict()
# create an instance of OptResultSet from a dict
opt_result_set_form_dict = opt_result_set.from_dict(opt_result_set_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



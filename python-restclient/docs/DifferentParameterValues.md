# DifferentParameterValues


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**parameter_name** | **str** |  | [optional] 
**original_value** | **str** |  | [optional] 
**changed_value** | **str** |  | [optional] 

## Example

```python
from vcell_client.models.different_parameter_values import DifferentParameterValues

# TODO update the JSON string below
json = "{}"
# create an instance of DifferentParameterValues from a JSON string
different_parameter_values_instance = DifferentParameterValues.from_json(json)
# print the JSON string representation of the object
print DifferentParameterValues.to_json()

# convert the object into a dict
different_parameter_values_dict = different_parameter_values_instance.to_dict()
# create an instance of DifferentParameterValues from a dict
different_parameter_values_form_dict = different_parameter_values.from_dict(different_parameter_values_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



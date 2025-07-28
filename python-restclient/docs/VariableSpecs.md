# VariableSpecs


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**variable_names** | **List[str]** |  | [optional] 
**mode** | [**VariableMode**](VariableMode.md) |  | [optional] 

## Example

```python
from vcell_client.models.variable_specs import VariableSpecs

# TODO update the JSON string below
json = "{}"
# create an instance of VariableSpecs from a JSON string
variable_specs_instance = VariableSpecs.from_json(json)
# print the JSON string representation of the object
print VariableSpecs.to_json()

# convert the object into a dict
variable_specs_dict = variable_specs_instance.to_dict()
# create an instance of VariableSpecs from a dict
variable_specs_form_dict = variable_specs.from_dict(variable_specs_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



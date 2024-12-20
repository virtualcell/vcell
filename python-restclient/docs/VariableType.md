# VariableType


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**type** | **int** |  | [optional] 
**variable_domain** | [**VariableDomain**](VariableDomain.md) |  | [optional] 
**name** | **str** |  | [optional] 
**units** | **str** |  | [optional] 
**label** | **str** |  | [optional] 
**legacy_warn** | **bool** |  | [optional] 
**default_label** | **str** |  | [optional] 
**default_units** | **str** |  | [optional] 
**type_name** | **str** |  | [optional] 

## Example

```python
from vcell_client.models.variable_type import VariableType

# TODO update the JSON string below
json = "{}"
# create an instance of VariableType from a JSON string
variable_type_instance = VariableType.from_json(json)
# print the JSON string representation of the object
print VariableType.to_json()

# convert the object into a dict
variable_type_dict = variable_type_instance.to_dict()
# create an instance of VariableType from a dict
variable_type_form_dict = variable_type.from_dict(variable_type_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



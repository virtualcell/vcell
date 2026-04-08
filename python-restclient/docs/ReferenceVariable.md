# ReferenceVariable


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**reference_variable_type** | [**ReferenceVariableReferenceVariableType**](ReferenceVariableReferenceVariableType.md) |  | [optional] 
**var_name** | **str** |  | [optional] 

## Example

```python
from vcell_client.models.reference_variable import ReferenceVariable

# TODO update the JSON string below
json = "{}"
# create an instance of ReferenceVariable from a JSON string
reference_variable_instance = ReferenceVariable.from_json(json)
# print the JSON string representation of the object
print ReferenceVariable.to_json()

# convert the object into a dict
reference_variable_dict = reference_variable_instance.to_dict()
# create an instance of ReferenceVariable from a dict
reference_variable_form_dict = reference_variable.from_dict(reference_variable_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



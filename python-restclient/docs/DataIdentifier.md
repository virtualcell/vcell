# DataIdentifier


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**name** | **str** |  | [optional] 
**display_name** | **str** |  | [optional] 
**variable_type** | [**VariableType**](VariableType.md) |  | [optional] 
**domain** | [**Domain**](Domain.md) |  | [optional] 
**b_function** | **bool** |  | [optional] 
**function** | **bool** |  | [optional] 
**visible** | **bool** |  | [optional] 

## Example

```python
from vcell_client.models.data_identifier import DataIdentifier

# TODO update the JSON string below
json = "{}"
# create an instance of DataIdentifier from a JSON string
data_identifier_instance = DataIdentifier.from_json(json)
# print the JSON string representation of the object
print DataIdentifier.to_json()

# convert the object into a dict
data_identifier_dict = data_identifier_instance.to_dict()
# create an instance of DataIdentifier from a dict
data_identifier_form_dict = data_identifier.from_dict(data_identifier_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



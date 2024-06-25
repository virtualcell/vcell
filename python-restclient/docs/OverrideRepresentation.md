# OverrideRepresentation


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**name** | **str** |  | [optional] 
**type** | **str** |  | [optional] 
**expression** | **str** |  | [optional] 
**values** | **List[str]** |  | [optional] 
**cardinality** | **int** |  | [optional] 

## Example

```python
from vcell_client.models.override_representation import OverrideRepresentation

# TODO update the JSON string below
json = "{}"
# create an instance of OverrideRepresentation from a JSON string
override_representation_instance = OverrideRepresentation.from_json(json)
# print the JSON string representation of the object
print OverrideRepresentation.to_json()

# convert the object into a dict
override_representation_dict = override_representation_instance.to_dict()
# create an instance of OverrideRepresentation from a dict
override_representation_form_dict = override_representation.from_dict(override_representation_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



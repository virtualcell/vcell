# VersionableType


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**v_type** | **int** |  | [optional] 
**name** | **str** |  | [optional] 
**v_class** | **object** |  | [optional] 
**b_top_level** | **bool** |  | [optional] 
**code** | **int** |  | [optional] 
**is_top_level** | **bool** |  | [optional] 
**type_name** | **str** |  | [optional] 
**version_class** | **object** |  | [optional] 

## Example

```python
from vcell_client.models.versionable_type import VersionableType

# TODO update the JSON string below
json = "{}"
# create an instance of VersionableType from a JSON string
versionable_type_instance = VersionableType.from_json(json)
# print the JSON string representation of the object
print VersionableType.to_json()

# convert the object into a dict
versionable_type_dict = versionable_type_instance.to_dict()
# create an instance of VersionableType from a dict
versionable_type_form_dict = versionable_type.from_dict(versionable_type_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



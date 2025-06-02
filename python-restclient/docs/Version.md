# Version


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**version_key** | **str** |  | [optional] 
**annot** | **str** |  | [optional] 
**branch_id** | **float** |  | [optional] 
**branch_point_ref_key** | **str** |  | [optional] 
**var_date** | **datetime** |  | [optional] 
**flag** | [**VersionFlag**](VersionFlag.md) |  | [optional] 
**group_access** | [**GroupAccess**](GroupAccess.md) |  | [optional] 
**name** | **str** |  | [optional] 
**owner** | [**User**](User.md) |  | [optional] 

## Example

```python
from vcell_client.models.version import Version

# TODO update the JSON string below
json = "{}"
# create an instance of Version from a JSON string
version_instance = Version.from_json(json)
# print the JSON string representation of the object
print Version.to_json()

# convert the object into a dict
version_dict = version_instance.to_dict()
# create an instance of Version from a dict
version_form_dict = version.from_dict(version_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



# VersionableTypeVersion


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**v_type** | [**VersionableType**](VersionableType.md) |  | [optional] 
**version** | [**Version**](Version.md) |  | [optional] 

## Example

```python
from vcell_client.models.versionable_type_version import VersionableTypeVersion

# TODO update the JSON string below
json = "{}"
# create an instance of VersionableTypeVersion from a JSON string
versionable_type_version_instance = VersionableTypeVersion.from_json(json)
# print the JSON string representation of the object
print VersionableTypeVersion.to_json()

# convert the object into a dict
versionable_type_version_dict = versionable_type_version_instance.to_dict()
# create an instance of VersionableTypeVersion from a dict
versionable_type_version_form_dict = versionable_type_version.from_dict(versionable_type_version_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



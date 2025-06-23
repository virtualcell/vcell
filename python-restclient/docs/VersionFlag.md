# VersionFlag


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**int_value** | **int** |  | [optional] 
**archived** | **bool** |  | [optional] 
**current** | **bool** |  | [optional] 
**published** | **bool** |  | [optional] 

## Example

```python
from vcell_client.models.version_flag import VersionFlag

# TODO update the JSON string below
json = "{}"
# create an instance of VersionFlag from a JSON string
version_flag_instance = VersionFlag.from_json(json)
# print the JSON string representation of the object
print VersionFlag.to_json()

# convert the object into a dict
version_flag_dict = version_flag_instance.to_dict()
# create an instance of VersionFlag from a dict
version_flag_form_dict = version_flag.from_dict(version_flag_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



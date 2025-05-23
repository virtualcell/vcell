# GroupAccess


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**groupid** | **float** |  | 
**description** | **str** |  | [optional] 

## Example

```python
from vcell_client.models.group_access import GroupAccess

# TODO update the JSON string below
json = "{}"
# create an instance of GroupAccess from a JSON string
group_access_instance = GroupAccess.from_json(json)
# print the JSON string representation of the object
print GroupAccess.to_json()

# convert the object into a dict
group_access_dict = group_access_instance.to_dict()
# create an instance of GroupAccess from a dict
group_access_form_dict = group_access.from_dict(group_access_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



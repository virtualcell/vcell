# GroupAccessSome


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**type** | **str** |  | [default to 'GroupAccessSome']
**hash** | **float** |  | [optional] 
**group_members** | [**List[User]**](User.md) |  | [optional] 
**hidden_members** | **List[bool]** |  | [optional] 
**description** | **str** |  | [optional] 
**hidden_group_members** | [**List[User]**](User.md) |  | [optional] 
**normal_group_members** | [**List[User]**](User.md) |  | [optional] 

## Example

```python
from vcell_client.models.group_access_some import GroupAccessSome

# TODO update the JSON string below
json = "{}"
# create an instance of GroupAccessSome from a JSON string
group_access_some_instance = GroupAccessSome.from_json(json)
# print the JSON string representation of the object
print GroupAccessSome.to_json()

# convert the object into a dict
group_access_some_dict = group_access_some_instance.to_dict()
# create an instance of GroupAccessSome from a dict
group_access_some_form_dict = group_access_some.from_dict(group_access_some_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



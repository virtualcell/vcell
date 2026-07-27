# GroupAccessRep


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**type** | **str** |  | [optional] 
**groupid** | **str** |  | [optional] 
**members** | [**List[GroupMemberRep]**](GroupMemberRep.md) |  | [optional] 

## Example

```python
from vcell_client.models.group_access_rep import GroupAccessRep

# TODO update the JSON string below
json = "{}"
# create an instance of GroupAccessRep from a JSON string
group_access_rep_instance = GroupAccessRep.from_json(json)
# print the JSON string representation of the object
print GroupAccessRep.to_json()

# convert the object into a dict
group_access_rep_dict = group_access_rep_instance.to_dict()
# create an instance of GroupAccessRep from a dict
group_access_rep_form_dict = group_access_rep.from_dict(group_access_rep_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



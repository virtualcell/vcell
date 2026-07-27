# GroupMemberRep


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**user** | [**UserRep**](UserRep.md) |  | [optional] 
**hidden** | **bool** |  | [optional] 

## Example

```python
from vcell_client.models.group_member_rep import GroupMemberRep

# TODO update the JSON string below
json = "{}"
# create an instance of GroupMemberRep from a JSON string
group_member_rep_instance = GroupMemberRep.from_json(json)
# print the JSON string representation of the object
print GroupMemberRep.to_json()

# convert the object into a dict
group_member_rep_dict = group_member_rep_instance.to_dict()
# create an instance of GroupMemberRep from a dict
group_member_rep_form_dict = group_member_rep.from_dict(group_member_rep_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



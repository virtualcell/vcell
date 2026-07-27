# VersionRep


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**version_key** | **str** |  | [optional] 
**name** | **str** |  | [optional] 
**owner** | [**UserRep**](UserRep.md) |  | [optional] 
**branch_point_ref_key** | **str** |  | [optional] 
**branch_id** | **str** |  | [optional] 
**var_date** | **int** |  | [optional] 
**flag** | **int** |  | [optional] 
**annotation** | **str** |  | [optional] 
**group_access** | [**GroupAccessRep**](GroupAccessRep.md) |  | [optional] 

## Example

```python
from vcell_client.models.version_rep import VersionRep

# TODO update the JSON string below
json = "{}"
# create an instance of VersionRep from a JSON string
version_rep_instance = VersionRep.from_json(json)
# print the JSON string representation of the object
print VersionRep.to_json()

# convert the object into a dict
version_rep_dict = version_rep_instance.to_dict()
# create an instance of VersionRep from a dict
version_rep_form_dict = version_rep.from_dict(version_rep_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



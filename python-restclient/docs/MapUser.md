# MapUser


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**user_id** | **str** |  | [optional] 
**password** | **str** |  | [optional] 

## Example

```python
from vcell_client.models.map_user import MapUser

# TODO update the JSON string below
json = "{}"
# create an instance of MapUser from a JSON string
map_user_instance = MapUser.from_json(json)
# print the JSON string representation of the object
print MapUser.to_json()

# convert the object into a dict
map_user_dict = map_user_instance.to_dict()
# create an instance of MapUser from a dict
map_user_form_dict = map_user.from_dict(map_user_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



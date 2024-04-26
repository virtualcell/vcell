# UserLoginInfoForMapping


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**user_id** | **str** |  | [optional] 
**password** | **str** |  | [optional] 

## Example

```python
from vcell_client.models.user_login_info_for_mapping import UserLoginInfoForMapping

# TODO update the JSON string below
json = "{}"
# create an instance of UserLoginInfoForMapping from a JSON string
user_login_info_for_mapping_instance = UserLoginInfoForMapping.from_json(json)
# print the JSON string representation of the object
print UserLoginInfoForMapping.to_json()

# convert the object into a dict
user_login_info_for_mapping_dict = user_login_info_for_mapping_instance.to_dict()
# create an instance of UserLoginInfoForMapping from a dict
user_login_info_for_mapping_form_dict = user_login_info_for_mapping.from_dict(user_login_info_for_mapping_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



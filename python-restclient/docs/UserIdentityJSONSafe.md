# UserIdentityJSONSafe


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**user_name** | **str** |  | [optional] 
**id** | **float** |  | [optional] 
**subject** | **str** |  | [optional] 
**insert_date** | **str** |  | [optional] 

## Example

```python
from vcell_client.models.user_identity_json_safe import UserIdentityJSONSafe

# TODO update the JSON string below
json = "{}"
# create an instance of UserIdentityJSONSafe from a JSON string
user_identity_json_safe_instance = UserIdentityJSONSafe.from_json(json)
# print the JSON string representation of the object
print UserIdentityJSONSafe.to_json()

# convert the object into a dict
user_identity_json_safe_dict = user_identity_json_safe_instance.to_dict()
# create an instance of UserIdentityJSONSafe from a dict
user_identity_json_safe_form_dict = user_identity_json_safe.from_dict(user_identity_json_safe_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



# UserRegistrationInfo


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**user_id** | **str** |  | [optional] 
**title** | **str** |  | [optional] 
**organization** | **str** |  | [optional] 
**country** | **str** |  | [optional] 
**email_notification** | **bool** |  | [optional] 

## Example

```python
from vcell_client.models.user_registration_info import UserRegistrationInfo

# TODO update the JSON string below
json = "{}"
# create an instance of UserRegistrationInfo from a JSON string
user_registration_info_instance = UserRegistrationInfo.from_json(json)
# print the JSON string representation of the object
print UserRegistrationInfo.to_json()

# convert the object into a dict
user_registration_info_dict = user_registration_info_instance.to_dict()
# create an instance of UserRegistrationInfo from a dict
user_registration_info_form_dict = user_registration_info.from_dict(user_registration_info_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



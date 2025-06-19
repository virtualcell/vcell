# SpecialUser


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**is_special** | **str** |  | [default to 'yes']
**my_specials** | [**List[SPECIALCLAIM]**](SPECIALCLAIM.md) |  | [optional] 

## Example

```python
from vcell_client.models.special_user import SpecialUser

# TODO update the JSON string below
json = "{}"
# create an instance of SpecialUser from a JSON string
special_user_instance = SpecialUser.from_json(json)
# print the JSON string representation of the object
print SpecialUser.to_json()

# convert the object into a dict
special_user_dict = special_user_instance.to_dict()
# create an instance of SpecialUser from a dict
special_user_form_dict = special_user.from_dict(special_user_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



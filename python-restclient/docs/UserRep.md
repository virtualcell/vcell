# UserRep


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**user_name** | **str** |  | [optional] 
**key** | **str** |  | [optional] 

## Example

```python
from vcell_client.models.user_rep import UserRep

# TODO update the JSON string below
json = "{}"
# create an instance of UserRep from a JSON string
user_rep_instance = UserRep.from_json(json)
# print the JSON string representation of the object
print UserRep.to_json()

# convert the object into a dict
user_rep_dict = user_rep_instance.to_dict()
# create an instance of UserRep from a dict
user_rep_form_dict = user_rep.from_dict(user_rep_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



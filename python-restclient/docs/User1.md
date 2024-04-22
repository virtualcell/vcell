# User1


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**user_name** | **str** |  | [optional] 
**key** | [**KeyValue**](KeyValue.md) |  | [optional] 
**i_d** | [**KeyValue**](KeyValue.md) |  | [optional] 
**name** | **str** |  | [optional] 
**publisher** | **bool** |  | [optional] 
**test_account** | **bool** |  | [optional] 

## Example

```python
from vcell_client.models.user1 import User1

# TODO update the JSON string below
json = "{}"
# create an instance of User1 from a JSON string
user1_instance = User1.from_json(json)
# print the JSON string representation of the object
print User1.to_json()

# convert the object into a dict
user1_dict = user1_instance.to_dict()
# create an instance of User1 from a dict
user1_form_dict = user1.from_dict(user1_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



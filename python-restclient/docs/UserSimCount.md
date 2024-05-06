# UserSimCount


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**username** | **str** |  | [optional] 
**sim_count** | **int** |  | [optional] 

## Example

```python
from vcell_client.models.user_sim_count import UserSimCount

# TODO update the JSON string below
json = "{}"
# create an instance of UserSimCount from a JSON string
user_sim_count_instance = UserSimCount.from_json(json)
# print the JSON string representation of the object
print UserSimCount.to_json()

# convert the object into a dict
user_sim_count_dict = user_sim_count_instance.to_dict()
# create an instance of UserSimCount from a dict
user_sim_count_form_dict = user_sim_count.from_dict(user_sim_count_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



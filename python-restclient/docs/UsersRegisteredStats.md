# UsersRegisteredStats


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**last1_week** | **int** |  | [optional] 
**last1_month** | **int** |  | [optional] 
**last3_months** | **int** |  | [optional] 
**last6_months** | **int** |  | [optional] 
**last12_months** | **int** |  | [optional] 

## Example

```python
from vcell_client.models.users_registered_stats import UsersRegisteredStats

# TODO update the JSON string below
json = "{}"
# create an instance of UsersRegisteredStats from a JSON string
users_registered_stats_instance = UsersRegisteredStats.from_json(json)
# print the JSON string representation of the object
print UsersRegisteredStats.to_json()

# convert the object into a dict
users_registered_stats_dict = users_registered_stats_instance.to_dict()
# create an instance of UsersRegisteredStats from a dict
users_registered_stats_form_dict = users_registered_stats.from_dict(users_registered_stats_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



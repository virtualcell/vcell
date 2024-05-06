# UsageSummary


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**sim_counts_7_days** | [**List[UserSimCount]**](UserSimCount.md) |  | [optional] 
**sim_counts_30_days** | [**List[UserSimCount]**](UserSimCount.md) |  | [optional] 
**sim_counts_90_days** | [**List[UserSimCount]**](UserSimCount.md) |  | [optional] 
**sim_counts_180_days** | [**List[UserSimCount]**](UserSimCount.md) |  | [optional] 
**sim_counts_365_days** | [**List[UserSimCount]**](UserSimCount.md) |  | [optional] 
**users_registered_stats** | [**UsersRegisteredStats**](UsersRegisteredStats.md) |  | [optional] 
**total_users** | **int** |  | [optional] 
**users_with_sims** | **int** |  | [optional] 
**biomodel_count** | **int** |  | [optional] 
**mathmodel_count** | **int** |  | [optional] 
**public_biomodel_count** | **int** |  | [optional] 
**public_mathmodel_count** | **int** |  | [optional] 
**sim_count** | **int** |  | [optional] 
**public_biomodel_sim_count** | **int** |  | [optional] 
**public_mathmodel_sim_count** | **int** |  | [optional] 

## Example

```python
from vcell_client.models.usage_summary import UsageSummary

# TODO update the JSON string below
json = "{}"
# create an instance of UsageSummary from a JSON string
usage_summary_instance = UsageSummary.from_json(json)
# print the JSON string representation of the object
print UsageSummary.to_json()

# convert the object into a dict
usage_summary_dict = usage_summary_instance.to_dict()
# create an instance of UsageSummary from a dict
usage_summary_form_dict = usage_summary.from_dict(usage_summary_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



# SavedResults


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**field_data_name** | **str** |  | [optional] 
**field_data_key** | **str** |  | [optional] 

## Example

```python
from vcell_client.models.saved_results import SavedResults

# TODO update the JSON string below
json = "{}"
# create an instance of SavedResults from a JSON string
saved_results_instance = SavedResults.from_json(json)
# print the JSON string representation of the object
print SavedResults.to_json()

# convert the object into a dict
saved_results_dict = saved_results_instance.to_dict()
# create an instance of SavedResults from a dict
saved_results_form_dict = saved_results.from_dict(saved_results_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



# FieldDataSavedResults


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**field_data_name** | **str** |  | [optional] 
**field_data_key** | **str** |  | [optional] 

## Example

```python
from vcell_client.models.field_data_saved_results import FieldDataSavedResults

# TODO update the JSON string below
json = "{}"
# create an instance of FieldDataSavedResults from a JSON string
field_data_saved_results_instance = FieldDataSavedResults.from_json(json)
# print the JSON string representation of the object
print FieldDataSavedResults.to_json()

# convert the object into a dict
field_data_saved_results_dict = field_data_saved_results_instance.to_dict()
# create an instance of FieldDataSavedResults from a dict
field_data_saved_results_form_dict = field_data_saved_results.from_dict(field_data_saved_results_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



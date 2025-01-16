# FieldDataSaveResults


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**field_data_name** | **str** |  | [optional] 
**field_data_id** | **str** |  | [optional] 

## Example

```python
from vcell_client.models.field_data_save_results import FieldDataSaveResults

# TODO update the JSON string below
json = "{}"
# create an instance of FieldDataSaveResults from a JSON string
field_data_save_results_instance = FieldDataSaveResults.from_json(json)
# print the JSON string representation of the object
print FieldDataSaveResults.to_json()

# convert the object into a dict
field_data_save_results_dict = field_data_save_results_instance.to_dict()
# create an instance of FieldDataSaveResults from a dict
field_data_save_results_form_dict = field_data_save_results.from_dict(field_data_save_results_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



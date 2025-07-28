# HumanReadableExportData


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**simulation_name** | **str** |  | [optional] 
**biomodel_name** | **str** |  | [optional] 
**application_name** | **str** |  | [optional] 
**different_parameter_values** | **List[str]** |  | [optional] 
**application_type** | **str** |  | [optional] 
**server_saved_file_name** | **str** |  | [optional] 
**non_spatial** | **bool** |  | [optional] 
**sub_volume** | **Dict[str, str]** |  | [optional] 
**z_slices** | **int** |  | [optional] 
**t_slices** | **int** |  | [optional] 
**num_channels** | **int** |  | [optional] 

## Example

```python
from vcell_client.models.human_readable_export_data import HumanReadableExportData

# TODO update the JSON string below
json = "{}"
# create an instance of HumanReadableExportData from a JSON string
human_readable_export_data_instance = HumanReadableExportData.from_json(json)
# print the JSON string representation of the object
print HumanReadableExportData.to_json()

# convert the object into a dict
human_readable_export_data_dict = human_readable_export_data_instance.to_dict()
# create an instance of HumanReadableExportData from a dict
human_readable_export_data_form_dict = human_readable_export_data.from_dict(human_readable_export_data_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



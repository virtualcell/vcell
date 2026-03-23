# ExportHistory


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**job_id** | **int** |  | [optional] 
**simulation_ref** | **str** |  | [optional] 
**export_format** | [**ExportFormat**](ExportFormat.md) |  | [optional] 
**export_date** | **str** |  | [optional] 
**uri** | **str** |  | [optional] 
**data_id_value** | **str** |  | [optional] 
**sim_name** | **str** |  | [optional] 
**app_name** | **str** |  | [optional] 
**bio_name** | **str** |  | [optional] 
**variables** | **List[str]** |  | [optional] 
**parameter_values** | [**List[DifferentParameterValues]**](DifferentParameterValues.md) |  | [optional] 
**start_time_value** | **float** |  | [optional] 
**end_time_value** | **float** |  | [optional] 
**saved_file_name_value** | **str** |  | [optional] 
**application_type_value** | **str** |  | [optional] 
**non_spatial_value** | **bool** |  | [optional] 
**z_slices_value** | **int** |  | [optional] 
**t_slices_value** | **int** |  | [optional] 
**num_variables_value** | **int** |  | [optional] 

## Example

```python
from vcell_client.models.export_history import ExportHistory

# TODO update the JSON string below
json = "{}"
# create an instance of ExportHistory from a JSON string
export_history_instance = ExportHistory.from_json(json)
# print the JSON string representation of the object
print ExportHistory.to_json()

# convert the object into a dict
export_history_dict = export_history_instance.to_dict()
# create an instance of ExportHistory from a dict
export_history_form_dict = export_history.from_dict(export_history_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



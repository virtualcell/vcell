# ExportHistory


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**export_job_id** | **int** |  | [optional] 
**simulation_ref** | **str** |  | [optional] 
**bio_model_ref** | **str** |  | [optional] 
**math_model_ref** | **str** |  | [optional] 
**math_ref** | **str** |  | [optional] 
**export_format** | [**ExportFormat**](ExportFormat.md) |  | [optional] 
**export_date** | **datetime** |  | [optional] 
**uri** | **str** |  | [optional] 
**sim_name** | **str** |  | [optional] 
**model_name** | **str** |  | [optional] 
**variables** | **List[str]** |  | [optional] 
**start_time_value** | **float** |  | [optional] 
**end_time_value** | **float** |  | [optional] 
**event_status** | [**ExportProgressType**](ExportProgressType.md) |  | [optional] 

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



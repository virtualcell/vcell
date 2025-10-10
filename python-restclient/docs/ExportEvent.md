# ExportEvent


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**event_type** | [**ExportProgressType**](ExportProgressType.md) |  | [optional] 
**progress** | **float** |  | [optional] 
**format** | **str** |  | [optional] 
**location** | **str** |  | [optional] 
**user** | [**User**](User.md) |  | [optional] 
**job_id** | **int** |  | [optional] 
**data_key** | **str** |  | [optional] 
**data_id_string** | **str** |  | [optional] 
**human_readable_data** | [**HumanReadableExportData**](HumanReadableExportData.md) |  | [optional] 

## Example

```python
from vcell_client.models.export_event import ExportEvent

# TODO update the JSON string below
json = "{}"
# create an instance of ExportEvent from a JSON string
export_event_instance = ExportEvent.from_json(json)
# print the JSON string representation of the object
print ExportEvent.to_json()

# convert the object into a dict
export_event_dict = export_event_instance.to_dict()
# create an instance of ExportEvent from a dict
export_event_form_dict = export_event.from_dict(export_event_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



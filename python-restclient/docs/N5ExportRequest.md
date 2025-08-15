# N5ExportRequest


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**standard_export_information** | [**StandardExportInfo**](StandardExportInfo.md) |  | [optional] 
**sub_volume** | **Dict[str, str]** |  | [optional] 
**exportable_data_type** | [**ExportableDataType**](ExportableDataType.md) |  | [optional] 
**dataset_name** | **str** |  | [optional] 

## Example

```python
from vcell_client.models.n5_export_request import N5ExportRequest

# TODO update the JSON string below
json = "{}"
# create an instance of N5ExportRequest from a JSON string
n5_export_request_instance = N5ExportRequest.from_json(json)
# print the JSON string representation of the object
print N5ExportRequest.to_json()

# convert the object into a dict
n5_export_request_dict = n5_export_request_instance.to_dict()
# create an instance of N5ExportRequest from a dict
n5_export_request_form_dict = n5_export_request.from_dict(n5_export_request_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



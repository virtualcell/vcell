# FieldDataReferenceInfo


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**reference_source_type** | **str** |  | [optional] 
**reference_source_name** | **str** |  | [optional] 
**application_name** | **str** |  | [optional] 
**simulation_name** | **str** |  | [optional] 
**ref_source_version_date** | **str** |  | [optional] 
**func_names** | **List[str]** |  | [optional] 
**ref_source_version_key** | [**KeyValue**](KeyValue.md) |  | [optional] 

## Example

```python
from vcell_client.models.field_data_reference_info import FieldDataReferenceInfo

# TODO update the JSON string below
json = "{}"
# create an instance of FieldDataReferenceInfo from a JSON string
field_data_reference_info_instance = FieldDataReferenceInfo.from_json(json)
# print the JSON string representation of the object
print FieldDataReferenceInfo.to_json()

# convert the object into a dict
field_data_reference_info_dict = field_data_reference_info_instance.to_dict()
# create an instance of FieldDataReferenceInfo from a dict
field_data_reference_info_form_dict = field_data_reference_info.from_dict(field_data_reference_info_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



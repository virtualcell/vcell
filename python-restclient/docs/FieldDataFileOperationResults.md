# FieldDataFileOperationResults


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**data_identifier_arr** | [**List[DataIdentifier]**](DataIdentifier.md) |  | [optional] 
**external_data_identifier** | [**ExternalDataIdentifier**](ExternalDataIdentifier.md) |  | [optional] 
**i_size** | [**ISize**](ISize.md) |  | [optional] 
**origin** | [**Origin**](Origin.md) |  | [optional] 
**extent** | [**Extent**](Extent.md) |  | [optional] 
**times** | **List[float]** |  | [optional] 
**dependant_function_info** | [**List[FieldDataReferenceInfo]**](FieldDataReferenceInfo.md) |  | [optional] 

## Example

```python
from vcell_client.models.field_data_file_operation_results import FieldDataFileOperationResults

# TODO update the JSON string below
json = "{}"
# create an instance of FieldDataFileOperationResults from a JSON string
field_data_file_operation_results_instance = FieldDataFileOperationResults.from_json(json)
# print the JSON string representation of the object
print FieldDataFileOperationResults.to_json()

# convert the object into a dict
field_data_file_operation_results_dict = field_data_file_operation_results_instance.to_dict()
# create an instance of FieldDataFileOperationResults from a dict
field_data_file_operation_results_form_dict = field_data_file_operation_results.from_dict(field_data_file_operation_results_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



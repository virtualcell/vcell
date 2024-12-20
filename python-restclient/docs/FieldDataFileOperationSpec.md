# FieldDataFileOperationSpec


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**op_type** | **int** |  | [optional] 
**short_spec_data** | **List[List[List[int]]]** |  | [optional] 
**double_spec_data** | **List[List[List[float]]]** |  | [optional] 
**cartesian_mesh** | [**CartesianMesh**](CartesianMesh.md) |  | [optional] 
**spec_edi** | [**ExternalDataIdentifier**](ExternalDataIdentifier.md) |  | [optional] 
**var_names** | **List[str]** |  | [optional] 
**variable_types** | [**List[VariableType]**](VariableType.md) |  | [optional] 
**times** | **List[float]** |  | [optional] 
**owner** | [**User**](User.md) |  | [optional] 
**origin** | [**Origin**](Origin.md) |  | [optional] 
**extent** | [**Extent**](Extent.md) |  | [optional] 
**isize** | [**ISize**](ISize.md) |  | [optional] 
**annotation** | **str** |  | [optional] 
**source_sim_param_scan_job_index** | **int** |  | [optional] 
**source_sim_data_key** | [**KeyValue**](KeyValue.md) |  | [optional] 
**source_owner** | [**User**](User.md) |  | [optional] 
**field_data_name** | **str** |  | [optional] 

## Example

```python
from vcell_client.models.field_data_file_operation_spec import FieldDataFileOperationSpec

# TODO update the JSON string below
json = "{}"
# create an instance of FieldDataFileOperationSpec from a JSON string
field_data_file_operation_spec_instance = FieldDataFileOperationSpec.from_json(json)
# print the JSON string representation of the object
print FieldDataFileOperationSpec.to_json()

# convert the object into a dict
field_data_file_operation_spec_dict = field_data_file_operation_spec_instance.to_dict()
# create an instance of FieldDataFileOperationSpec from a dict
field_data_file_operation_spec_form_dict = field_data_file_operation_spec.from_dict(field_data_file_operation_spec_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



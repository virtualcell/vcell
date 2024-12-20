# FieldDataDBOperationSpec


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**op_type** | **int** |  | [optional] 
**spec_edi** | [**ExternalDataIdentifier**](ExternalDataIdentifier.md) |  | [optional] 
**owner** | [**User**](User.md) |  | [optional] 
**new_ext_data_id_name** | **str** |  | [optional] 
**annotation** | **str** |  | [optional] 
**source_names** | **List[str]** |  | [optional] 
**source_owner** | [**VersionableTypeVersion**](VersionableTypeVersion.md) |  | [optional] 
**b_include_sim_refs** | **bool** |  | [optional] 

## Example

```python
from vcell_client.models.field_data_db_operation_spec import FieldDataDBOperationSpec

# TODO update the JSON string below
json = "{}"
# create an instance of FieldDataDBOperationSpec from a JSON string
field_data_db_operation_spec_instance = FieldDataDBOperationSpec.from_json(json)
# print the JSON string representation of the object
print FieldDataDBOperationSpec.to_json()

# convert the object into a dict
field_data_db_operation_spec_dict = field_data_db_operation_spec_instance.to_dict()
# create an instance of FieldDataDBOperationSpec from a dict
field_data_db_operation_spec_form_dict = field_data_db_operation_spec.from_dict(field_data_db_operation_spec_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



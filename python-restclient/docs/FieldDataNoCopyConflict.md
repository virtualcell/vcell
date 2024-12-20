# FieldDataNoCopyConflict


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**old_name_new_id_hash** | [**Dict[str, ExternalDataIdentifier]**](ExternalDataIdentifier.md) |  | [optional] 
**old_name_old_ext_data_id_key_hash** | [**Dict[str, KeyValue]**](KeyValue.md) |  | [optional] 

## Example

```python
from vcell_client.models.field_data_no_copy_conflict import FieldDataNoCopyConflict

# TODO update the JSON string below
json = "{}"
# create an instance of FieldDataNoCopyConflict from a JSON string
field_data_no_copy_conflict_instance = FieldDataNoCopyConflict.from_json(json)
# print the JSON string representation of the object
print FieldDataNoCopyConflict.to_json()

# convert the object into a dict
field_data_no_copy_conflict_dict = field_data_no_copy_conflict_instance.to_dict()
# create an instance of FieldDataNoCopyConflict from a dict
field_data_no_copy_conflict_form_dict = field_data_no_copy_conflict.from_dict(field_data_no_copy_conflict_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



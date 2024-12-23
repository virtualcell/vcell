# FieldDataReferences


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**external_data_identifiers** | [**List[ExternalDataIdentifier]**](ExternalDataIdentifier.md) |  | [optional] 
**external_data_annotations** | **List[str]** |  | [optional] 
**external_data_id_sim_refs** | **Dict[str, List[KeyValue]]** |  | [optional] 

## Example

```python
from vcell_client.models.field_data_references import FieldDataReferences

# TODO update the JSON string below
json = "{}"
# create an instance of FieldDataReferences from a JSON string
field_data_references_instance = FieldDataReferences.from_json(json)
# print the JSON string representation of the object
print FieldDataReferences.to_json()

# convert the object into a dict
field_data_references_dict = field_data_references_instance.to_dict()
# create an instance of FieldDataReferences from a dict
field_data_references_form_dict = field_data_references.from_dict(field_data_references_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



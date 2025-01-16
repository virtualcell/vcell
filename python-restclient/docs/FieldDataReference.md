# FieldDataReference


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**external_data_identifier** | [**ExternalDataIdentifier**](ExternalDataIdentifier.md) |  | [optional] 
**external_data_annotation** | **str** |  | [optional] 
**external_data_id_sim_refs** | [**List[KeyValue]**](KeyValue.md) |  | [optional] 

## Example

```python
from vcell_client.models.field_data_reference import FieldDataReference

# TODO update the JSON string below
json = "{}"
# create an instance of FieldDataReference from a JSON string
field_data_reference_instance = FieldDataReference.from_json(json)
# print the JSON string representation of the object
print FieldDataReference.to_json()

# convert the object into a dict
field_data_reference_dict = field_data_reference_instance.to_dict()
# create an instance of FieldDataReference from a dict
field_data_reference_form_dict = field_data_reference.from_dict(field_data_reference_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



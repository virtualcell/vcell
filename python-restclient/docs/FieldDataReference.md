# FieldDataReference


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**field_data_id** | [**ExternalDataIdentifier**](ExternalDataIdentifier.md) |  | [optional] 
**annotation** | **str** |  | [optional] 
**simulations_referencing_this_id** | [**List[KeyValue]**](KeyValue.md) |  | [optional] 

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



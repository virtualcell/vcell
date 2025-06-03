# ExternalDataIdentifier


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**key** | **str** |  | [optional] 
**owner** | [**User**](User.md) |  | [optional] 
**name** | **str** |  | [optional] 
**job_index** | **int** |  | [optional] 
**simulation_key** | **str** |  | [optional] 
**parameter_scan_type** | **bool** |  | [optional] 
**data_key** | **str** |  | [optional] 

## Example

```python
from vcell_client.models.external_data_identifier import ExternalDataIdentifier

# TODO update the JSON string below
json = "{}"
# create an instance of ExternalDataIdentifier from a JSON string
external_data_identifier_instance = ExternalDataIdentifier.from_json(json)
# print the JSON string representation of the object
print ExternalDataIdentifier.to_json()

# convert the object into a dict
external_data_identifier_dict = external_data_identifier_instance.to_dict()
# create an instance of ExternalDataIdentifier from a dict
external_data_identifier_form_dict = external_data_identifier.from_dict(external_data_identifier_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



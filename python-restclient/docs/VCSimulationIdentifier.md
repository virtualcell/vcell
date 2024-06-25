# VCSimulationIdentifier


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**simulation_key** | [**KeyValue**](KeyValue.md) |  | [optional] 
**owner** | [**User**](User.md) |  | [optional] 
**i_d** | **str** |  | [optional] 

## Example

```python
from vcell_client.models.vc_simulation_identifier import VCSimulationIdentifier

# TODO update the JSON string below
json = "{}"
# create an instance of VCSimulationIdentifier from a JSON string
vc_simulation_identifier_instance = VCSimulationIdentifier.from_json(json)
# print the JSON string representation of the object
print VCSimulationIdentifier.to_json()

# convert the object into a dict
vc_simulation_identifier_dict = vc_simulation_identifier_instance.to_dict()
# create an instance of VCSimulationIdentifier from a dict
vc_simulation_identifier_form_dict = vc_simulation_identifier.from_dict(vc_simulation_identifier_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



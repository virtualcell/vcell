# SimulationMessage


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**detailed_state** | [**DetailedState**](DetailedState.md) |  | [optional] 
**message** | **str** |  | [optional] 
**htc_job_id** | [**HtcJobID**](HtcJobID.md) |  | [optional] 
**display_message** | **str** |  | [optional] 

## Example

```python
from vcell_client.models.simulation_message import SimulationMessage

# TODO update the JSON string below
json = "{}"
# create an instance of SimulationMessage from a JSON string
simulation_message_instance = SimulationMessage.from_json(json)
# print the JSON string representation of the object
print SimulationMessage.to_json()

# convert the object into a dict
simulation_message_dict = simulation_message_instance.to_dict()
# create an instance of SimulationMessage from a dict
simulation_message_form_dict = simulation_message.from_dict(simulation_message_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



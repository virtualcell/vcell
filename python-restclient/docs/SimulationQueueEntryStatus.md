# SimulationQueueEntryStatus


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**field_queue_priority** | **int** |  | [optional] 
**field_queue_date** | **date** |  | [optional] 
**field_queue_id** | [**SimulationQueueID**](SimulationQueueID.md) |  | [optional] 
**queue_date** | **date** |  | [optional] 
**queue_id** | [**SimulationQueueID**](SimulationQueueID.md) |  | [optional] 
**queue_priority** | **int** |  | [optional] 

## Example

```python
from vcell_client.models.simulation_queue_entry_status import SimulationQueueEntryStatus

# TODO update the JSON string below
json = "{}"
# create an instance of SimulationQueueEntryStatus from a JSON string
simulation_queue_entry_status_instance = SimulationQueueEntryStatus.from_json(json)
# print the JSON string representation of the object
print SimulationQueueEntryStatus.to_json()

# convert the object into a dict
simulation_queue_entry_status_dict = simulation_queue_entry_status_instance.to_dict()
# create an instance of SimulationQueueEntryStatus from a dict
simulation_queue_entry_status_form_dict = simulation_queue_entry_status.from_dict(simulation_queue_entry_status_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



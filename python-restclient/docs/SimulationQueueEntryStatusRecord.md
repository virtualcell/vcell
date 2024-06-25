# SimulationQueueEntryStatusRecord


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**field_queue_priority** | **int** |  | [optional] 
**field_queue_date** | **datetime** |  | [optional] 
**field_queue_id** | [**SimulationQueueID**](SimulationQueueID.md) |  | [optional] 

## Example

```python
from vcell_client.models.simulation_queue_entry_status_record import SimulationQueueEntryStatusRecord

# TODO update the JSON string below
json = "{}"
# create an instance of SimulationQueueEntryStatusRecord from a JSON string
simulation_queue_entry_status_record_instance = SimulationQueueEntryStatusRecord.from_json(json)
# print the JSON string representation of the object
print SimulationQueueEntryStatusRecord.to_json()

# convert the object into a dict
simulation_queue_entry_status_record_dict = simulation_queue_entry_status_record_instance.to_dict()
# create an instance of SimulationQueueEntryStatusRecord from a dict
simulation_queue_entry_status_record_form_dict = simulation_queue_entry_status_record.from_dict(simulation_queue_entry_status_record_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



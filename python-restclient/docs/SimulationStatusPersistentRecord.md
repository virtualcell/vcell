# SimulationStatusPersistentRecord


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**status** | [**Status**](Status.md) |  | [optional] 
**details** | **str** |  | [optional] 
**has_data** | **bool** |  | [optional] 

## Example

```python
from vcell_client.models.simulation_status_persistent_record import SimulationStatusPersistentRecord

# TODO update the JSON string below
json = "{}"
# create an instance of SimulationStatusPersistentRecord from a JSON string
simulation_status_persistent_record_instance = SimulationStatusPersistentRecord.from_json(json)
# print the JSON string representation of the object
print SimulationStatusPersistentRecord.to_json()

# convert the object into a dict
simulation_status_persistent_record_dict = simulation_status_persistent_record_instance.to_dict()
# create an instance of SimulationStatusPersistentRecord from a dict
simulation_status_persistent_record_form_dict = simulation_status_persistent_record.from_dict(simulation_status_persistent_record_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



# SimulationExecutionStatusRecord


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**field_start_date** | **datetime** |  | [optional] 
**field_latest_update_date** | **datetime** |  | [optional] 
**field_end_date** | **datetime** |  | [optional] 
**field_compute_host** | **str** |  | [optional] 
**field_has_data** | **bool** |  | [optional] 
**field_htc_job_id** | [**HtcJobID**](HtcJobID.md) |  | [optional] 

## Example

```python
from vcell_client.models.simulation_execution_status_record import SimulationExecutionStatusRecord

# TODO update the JSON string below
json = "{}"
# create an instance of SimulationExecutionStatusRecord from a JSON string
simulation_execution_status_record_instance = SimulationExecutionStatusRecord.from_json(json)
# print the JSON string representation of the object
print SimulationExecutionStatusRecord.to_json()

# convert the object into a dict
simulation_execution_status_record_dict = simulation_execution_status_record_instance.to_dict()
# create an instance of SimulationExecutionStatusRecord from a dict
simulation_execution_status_record_form_dict = simulation_execution_status_record.from_dict(simulation_execution_status_record_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



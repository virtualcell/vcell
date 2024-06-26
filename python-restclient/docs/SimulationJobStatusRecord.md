# SimulationJobStatusRecord


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**field_time_data_stamp** | **datetime** |  | [optional] 
**field_vc_sim_id** | [**VCSimulationIdentifier**](VCSimulationIdentifier.md) |  | [optional] 
**field_submit_date** | **datetime** |  | [optional] 
**field_scheduler_status** | [**SchedulerStatus**](SchedulerStatus.md) |  | [optional] 
**field_simulation_message** | [**SimulationMessage**](SimulationMessage.md) |  | [optional] 
**field_task_id** | **int** |  | [optional] 
**field_server_id** | **str** |  | [optional] 
**field_job_index** | **int** |  | [optional] 
**field_simulation_execution_status** | [**SimulationExecutionStatusRecord**](SimulationExecutionStatusRecord.md) |  | [optional] 
**field_simulation_queue_entry_status** | [**SimulationQueueEntryStatusRecord**](SimulationQueueEntryStatusRecord.md) |  | [optional] 

## Example

```python
from vcell_client.models.simulation_job_status_record import SimulationJobStatusRecord

# TODO update the JSON string below
json = "{}"
# create an instance of SimulationJobStatusRecord from a JSON string
simulation_job_status_record_instance = SimulationJobStatusRecord.from_json(json)
# print the JSON string representation of the object
print SimulationJobStatusRecord.to_json()

# convert the object into a dict
simulation_job_status_record_dict = simulation_job_status_record_instance.to_dict()
# create an instance of SimulationJobStatusRecord from a dict
simulation_job_status_record_form_dict = simulation_job_status_record.from_dict(simulation_job_status_record_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



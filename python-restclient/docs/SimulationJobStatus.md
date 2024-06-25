# SimulationJobStatus


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**field_time_date_stamp** | **date** |  | [optional] 
**field_vc_sim_id** | [**VCSimulationIdentifier**](VCSimulationIdentifier.md) |  | [optional] 
**field_submit_date** | **date** |  | [optional] 
**field_scheduler_status** | [**SchedulerStatus**](SchedulerStatus.md) |  | [optional] 
**field_task_id** | **int** |  | [optional] 
**field_simulation_message** | [**SimulationMessage**](SimulationMessage.md) |  | [optional] 
**field_server_id** | [**VCellServerID**](VCellServerID.md) |  | [optional] 
**field_job_index** | **int** |  | [optional] 
**field_simulation_queue_entry_status** | [**SimulationQueueEntryStatus**](SimulationQueueEntryStatus.md) |  | [optional] 
**field_simulation_execution_status** | [**SimulationExecutionStatus**](SimulationExecutionStatus.md) |  | [optional] 
**compute_host** | **str** |  | [optional] 
**end_date** | **date** |  | [optional] 
**job_index** | **int** |  | [optional] 
**scheduler_status** | [**SchedulerStatus**](SchedulerStatus.md) |  | [optional] 
**server_id** | [**VCellServerID**](VCellServerID.md) |  | [optional] 
**simulation_execution_status** | [**SimulationExecutionStatus**](SimulationExecutionStatus.md) |  | [optional] 
**simulation_queue_entry_status** | [**SimulationQueueEntryStatus**](SimulationQueueEntryStatus.md) |  | [optional] 
**start_date** | **date** |  | [optional] 
**simulation_message** | [**SimulationMessage**](SimulationMessage.md) |  | [optional] 
**submit_date** | **date** |  | [optional] 
**task_id** | **int** |  | [optional] 
**time_date_stamp** | **date** |  | [optional] 
**v_c_simulation_identifier** | [**VCSimulationIdentifier**](VCSimulationIdentifier.md) |  | [optional] 

## Example

```python
from vcell_client.models.simulation_job_status import SimulationJobStatus

# TODO update the JSON string below
json = "{}"
# create an instance of SimulationJobStatus from a JSON string
simulation_job_status_instance = SimulationJobStatus.from_json(json)
# print the JSON string representation of the object
print SimulationJobStatus.to_json()

# convert the object into a dict
simulation_job_status_dict = simulation_job_status_instance.to_dict()
# create an instance of SimulationJobStatus from a dict
simulation_job_status_form_dict = simulation_job_status.from_dict(simulation_job_status_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



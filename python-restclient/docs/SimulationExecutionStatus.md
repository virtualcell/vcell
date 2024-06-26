# SimulationExecutionStatus


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**field_start_date** | **date** |  | [optional] 
**field_latest_update_date** | **date** |  | [optional] 
**field_end_date** | **date** |  | [optional] 
**field_compute_host** | **str** |  | [optional] 
**field_has_data** | **bool** |  | [optional] 
**field_htc_job_id** | [**HtcJobID**](HtcJobID.md) |  | [optional] 
**compute_host** | **str** |  | [optional] 
**end_date** | **date** |  | [optional] 
**latest_update_date** | **date** |  | [optional] 
**start_date** | **date** |  | [optional] 
**htc_job_id** | [**HtcJobID**](HtcJobID.md) |  | [optional] 

## Example

```python
from vcell_client.models.simulation_execution_status import SimulationExecutionStatus

# TODO update the JSON string below
json = "{}"
# create an instance of SimulationExecutionStatus from a JSON string
simulation_execution_status_instance = SimulationExecutionStatus.from_json(json)
# print the JSON string representation of the object
print SimulationExecutionStatus.to_json()

# convert the object into a dict
simulation_execution_status_dict = simulation_execution_status_instance.to_dict()
# create an instance of SimulationExecutionStatus from a dict
simulation_execution_status_form_dict = simulation_execution_status.from_dict(simulation_execution_status_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



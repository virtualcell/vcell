# OptimizationJobStatus


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **str** |  | [optional] 
**status** | [**OptJobStatus**](OptJobStatus.md) |  | [optional] 
**status_message** | **str** |  | [optional] 
**htc_job_id** | **str** |  | [optional] 
**progress_report** | [**OptProgressReport**](OptProgressReport.md) |  | [optional] 
**results** | [**Vcellopt**](Vcellopt.md) |  | [optional] 

## Example

```python
from vcell_client.models.optimization_job_status import OptimizationJobStatus

# TODO update the JSON string below
json = "{}"
# create an instance of OptimizationJobStatus from a JSON string
optimization_job_status_instance = OptimizationJobStatus.from_json(json)
# print the JSON string representation of the object
print OptimizationJobStatus.to_json()

# convert the object into a dict
optimization_job_status_dict = optimization_job_status_instance.to_dict()
# create an instance of OptimizationJobStatus from a dict
optimization_job_status_form_dict = optimization_job_status.from_dict(optimization_job_status_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



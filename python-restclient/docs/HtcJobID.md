# HtcJobID


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**job_number** | **int** |  | [optional] 
**server** | **str** |  | [optional] 
**batch_system_type** | [**BatchSystemType**](BatchSystemType.md) |  | [optional] 

## Example

```python
from vcell_client.models.htc_job_id import HtcJobID

# TODO update the JSON string below
json = "{}"
# create an instance of HtcJobID from a JSON string
htc_job_id_instance = HtcJobID.from_json(json)
# print the JSON string representation of the object
print HtcJobID.to_json()

# convert the object into a dict
htc_job_id_dict = htc_job_id_instance.to_dict()
# create an instance of HtcJobID from a dict
htc_job_id_form_dict = htc_job_id.from_dict(htc_job_id_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



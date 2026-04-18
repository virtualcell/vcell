# OptProgressReport


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**best_param_values** | **Dict[str, float]** |  | [optional] 
**progress_items** | [**List[OptProgressItem]**](OptProgressItem.md) |  | [optional] 

## Example

```python
from vcell_client.models.opt_progress_report import OptProgressReport

# TODO update the JSON string below
json = "{}"
# create an instance of OptProgressReport from a JSON string
opt_progress_report_instance = OptProgressReport.from_json(json)
# print the JSON string representation of the object
print OptProgressReport.to_json()

# convert the object into a dict
opt_progress_report_dict = opt_progress_report_instance.to_dict()
# create an instance of OptProgressReport from a dict
opt_progress_report_form_dict = opt_progress_report.from_dict(opt_progress_report_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



# TimeSpecs


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**begin_time_index** | **int** |  | [optional] 
**end_time_index** | **int** |  | [optional] 
**all_times** | **List[float]** |  | [optional] 
**mode** | [**TimeMode**](TimeMode.md) |  | [optional] 

## Example

```python
from vcell_client.models.time_specs import TimeSpecs

# TODO update the JSON string below
json = "{}"
# create an instance of TimeSpecs from a JSON string
time_specs_instance = TimeSpecs.from_json(json)
# print the JSON string representation of the object
print TimeSpecs.to_json()

# convert the object into a dict
time_specs_dict = time_specs_instance.to_dict()
# create an instance of TimeSpecs from a dict
time_specs_form_dict = time_specs.from_dict(time_specs_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



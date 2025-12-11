# SampledCurve


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**type** | **str** |  | [default to 'SampledCurve']
**default_num_samples** | **int** |  | [optional] 
**max_control_points** | **int** |  | [optional] 
**min_control_points** | **int** |  | [optional] 
**segment_count** | **int** |  | [optional] 
**spatial_length** | **float** |  | [optional] 

## Example

```python
from vcell_client.models.sampled_curve import SampledCurve

# TODO update the JSON string below
json = "{}"
# create an instance of SampledCurve from a JSON string
sampled_curve_instance = SampledCurve.from_json(json)
# print the JSON string representation of the object
print SampledCurve.to_json()

# convert the object into a dict
sampled_curve_dict = sampled_curve_instance.to_dict()
# create an instance of SampledCurve from a dict
sampled_curve_form_dict = sampled_curve.from_dict(sampled_curve_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



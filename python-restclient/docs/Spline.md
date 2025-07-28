# Spline


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**type** | **str** |  | [default to 'Spline']
**default_num_samples** | **int** |  | [optional] 
**max_control_points** | **int** |  | [optional] 
**min_control_points** | **int** |  | [optional] 
**segment_count** | **int** |  | [optional] 

## Example

```python
from vcell_client.models.spline import Spline

# TODO update the JSON string below
json = "{}"
# create an instance of Spline from a JSON string
spline_instance = Spline.from_json(json)
# print the JSON string representation of the object
print Spline.to_json()

# convert the object into a dict
spline_dict = spline_instance.to_dict()
# create an instance of Spline from a dict
spline_form_dict = spline.from_dict(spline_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



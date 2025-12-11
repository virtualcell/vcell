# Curve


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**b_closed** | **bool** |  | [optional] 
**description** | **str** |  | [optional] 
**type** | **str** |  | 
**beginning_coordinate** | [**Coordinate**](Coordinate.md) |  | [optional] 
**default_num_samples** | **int** |  | [optional] 
**ending_coordinate** | [**Coordinate**](Coordinate.md) |  | [optional] 
**num_sample_points** | **int** |  | [optional] 
**segment_count** | **int** |  | [optional] 
**spatial_length** | **float** |  | [optional] 
**closed** | **bool** |  | [optional] 
**valid** | **bool** |  | [optional] 

## Example

```python
from vcell_client.models.curve import Curve

# TODO update the JSON string below
json = "{}"
# create an instance of Curve from a JSON string
curve_instance = Curve.from_json(json)
# print the JSON string representation of the object
print Curve.to_json()

# convert the object into a dict
curve_dict = curve_instance.to_dict()
# create an instance of Curve from a dict
curve_form_dict = curve.from_dict(curve_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



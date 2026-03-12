# ControlPointCurve


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**type** | **object** |  | 
**control_points** | **object** |  | [optional] 
**control_point_count** | **int** |  | [optional] 
**control_points_vector** | **object** |  | [optional] 
**max_control_points** | **int** |  | [optional] 
**min_control_points** | **int** |  | [optional] 
**control_point_addable** | **bool** |  | [optional] 
**valid** | **bool** |  | [optional] 

## Example

```python
from vcell_client.models.control_point_curve import ControlPointCurve

# TODO update the JSON string below
json = "{}"
# create an instance of ControlPointCurve from a JSON string
control_point_curve_instance = ControlPointCurve.from_json(json)
# print the JSON string representation of the object
print ControlPointCurve.to_json()

# convert the object into a dict
control_point_curve_dict = control_point_curve_instance.to_dict()
# create an instance of ControlPointCurve from a dict
control_point_curve_form_dict = control_point_curve.from_dict(control_point_curve_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



# CurveSelectionInfo


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**field_curve** | [**Curve**](Curve.md) |  | [optional] 
**field_type** | **int** |  | [optional] 
**field_control_point** | **int** |  | [optional] 
**field_segment** | **int** |  | [optional] 
**field_u** | **float** |  | [optional] 
**field_u_extended** | **float** |  | [optional] 
**field_control_point_extended** | **int** |  | [optional] 
**field_segment_extended** | **int** |  | [optional] 
**field_direction_negative** | **bool** |  | [optional] 
**crossing** | **bool** |  | [optional] 

## Example

```python
from vcell_client.models.curve_selection_info import CurveSelectionInfo

# TODO update the JSON string below
json = "{}"
# create an instance of CurveSelectionInfo from a JSON string
curve_selection_info_instance = CurveSelectionInfo.from_json(json)
# print the JSON string representation of the object
print CurveSelectionInfo.to_json()

# convert the object into a dict
curve_selection_info_dict = curve_selection_info_instance.to_dict()
# create an instance of CurveSelectionInfo from a dict
curve_selection_info_form_dict = curve_selection_info.from_dict(curve_selection_info_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



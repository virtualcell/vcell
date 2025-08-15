# AnalyticCurve


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**type** | **str** |  | [default to 'AnalyticCurve']
**exp_x** | **str** |  | [optional] 
**exp_y** | **str** |  | [optional] 
**exp_z** | **str** |  | [optional] 
**offset** | [**Coordinate**](Coordinate.md) |  | [optional] 
**analytic_offset** | [**Coordinate**](Coordinate.md) |  | [optional] 
**default_num_samples** | **int** |  | [optional] 
**segment_count** | **int** |  | [optional] 
**valid** | **bool** |  | [optional] 

## Example

```python
from vcell_client.models.analytic_curve import AnalyticCurve

# TODO update the JSON string below
json = "{}"
# create an instance of AnalyticCurve from a JSON string
analytic_curve_instance = AnalyticCurve.from_json(json)
# print the JSON string representation of the object
print AnalyticCurve.to_json()

# convert the object into a dict
analytic_curve_dict = analytic_curve_instance.to_dict()
# create an instance of AnalyticCurve from a dict
analytic_curve_form_dict = analytic_curve.from_dict(analytic_curve_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



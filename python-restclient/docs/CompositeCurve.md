# CompositeCurve


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**type** | **str** |  | [default to 'CompositeCurve']
**field_curves** | **List[object]** |  | [optional] 
**curve_count** | **int** |  | [optional] 
**default_num_samples** | **int** |  | [optional] 
**segment_count** | **int** |  | [optional] 
**valid** | **bool** |  | [optional] 

## Example

```python
from vcell_client.models.composite_curve import CompositeCurve

# TODO update the JSON string below
json = "{}"
# create an instance of CompositeCurve from a JSON string
composite_curve_instance = CompositeCurve.from_json(json)
# print the JSON string representation of the object
print CompositeCurve.to_json()

# convert the object into a dict
composite_curve_dict = composite_curve_instance.to_dict()
# create an instance of CompositeCurve from a dict
composite_curve_form_dict = composite_curve.from_dict(composite_curve_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



# SpatialSelectionMembrane


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**type** | **str** |  | [default to 'Membrane']
**field_sampled_data_indexes** | **List[int]** |  | [optional] 
**selection_source** | [**SampledCurve**](SampledCurve.md) |  | [optional] 

## Example

```python
from vcell_client.models.spatial_selection_membrane import SpatialSelectionMembrane

# TODO update the JSON string below
json = "{}"
# create an instance of SpatialSelectionMembrane from a JSON string
spatial_selection_membrane_instance = SpatialSelectionMembrane.from_json(json)
# print the JSON string representation of the object
print SpatialSelectionMembrane.to_json()

# convert the object into a dict
spatial_selection_membrane_dict = spatial_selection_membrane_instance.to_dict()
# create an instance of SpatialSelectionMembrane from a dict
spatial_selection_membrane_form_dict = spatial_selection_membrane.from_dict(spatial_selection_membrane_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



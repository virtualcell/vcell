# SpatialSelectionContour


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**type** | **str** |  | [optional] [default to 'Contour']
**field_sampled_data_indexes** | **List[int]** |  | [optional] 
**index_samples** | **List[int]** |  | [optional] 
**sampled_data_indexes** | **List[int]** |  | [optional] 

## Example

```python
from vcell_client.models.spatial_selection_contour import SpatialSelectionContour

# TODO update the JSON string below
json = "{}"
# create an instance of SpatialSelectionContour from a JSON string
spatial_selection_contour_instance = SpatialSelectionContour.from_json(json)
# print the JSON string representation of the object
print SpatialSelectionContour.to_json()

# convert the object into a dict
spatial_selection_contour_dict = spatial_selection_contour_instance.to_dict()
# create an instance of SpatialSelectionContour from a dict
spatial_selection_contour_form_dict = spatial_selection_contour.from_dict(spatial_selection_contour_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



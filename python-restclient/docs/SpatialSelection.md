# SpatialSelection


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**curve_selection_info** | [**CurveSelectionInfo**](CurveSelectionInfo.md) |  | [optional] 
**var_type** | [**VariableType**](VariableType.md) |  | [optional] 
**type** | **str** |  | 
**smallest_mesh_cell_dimension_length** | **float** |  | [optional] 
**variable_type** | [**VariableType**](VariableType.md) |  | [optional] 
**closed** | **bool** |  | [optional] 
**point** | **bool** |  | [optional] 

## Example

```python
from vcell_client.models.spatial_selection import SpatialSelection

# TODO update the JSON string below
json = "{}"
# create an instance of SpatialSelection from a JSON string
spatial_selection_instance = SpatialSelection.from_json(json)
# print the JSON string representation of the object
print SpatialSelection.to_json()

# convert the object into a dict
spatial_selection_dict = spatial_selection_instance.to_dict()
# create an instance of SpatialSelection from a dict
spatial_selection_form_dict = spatial_selection.from_dict(spatial_selection_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



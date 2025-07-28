# SpatialSelectionVolume


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**type** | **str** |  | [optional] [default to 'Volume']
**symmetric** | **bool** |  | [optional] 

## Example

```python
from vcell_client.models.spatial_selection_volume import SpatialSelectionVolume

# TODO update the JSON string below
json = "{}"
# create an instance of SpatialSelectionVolume from a JSON string
spatial_selection_volume_instance = SpatialSelectionVolume.from_json(json)
# print the JSON string representation of the object
print SpatialSelectionVolume.to_json()

# convert the object into a dict
spatial_selection_volume_dict = spatial_selection_volume_instance.to_dict()
# create an instance of SpatialSelectionVolume from a dict
spatial_selection_volume_form_dict = spatial_selection_volume.from_dict(spatial_selection_volume_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



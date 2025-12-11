# GeometrySpecDTO


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**selections** | [**List[SpatialSelection]**](SpatialSelection.md) |  | [optional] 
**axis** | **int** |  | [optional] 
**slice_number** | **int** |  | [optional] 
**geometry_mode** | [**GeometryMode**](GeometryMode.md) |  | [optional] 

## Example

```python
from vcell_client.models.geometry_spec_dto import GeometrySpecDTO

# TODO update the JSON string below
json = "{}"
# create an instance of GeometrySpecDTO from a JSON string
geometry_spec_dto_instance = GeometrySpecDTO.from_json(json)
# print the JSON string representation of the object
print GeometrySpecDTO.to_json()

# convert the object into a dict
geometry_spec_dto_dict = geometry_spec_dto_instance.to_dict()
# create an instance of GeometrySpecDTO from a dict
geometry_spec_dto_form_dict = geometry_spec_dto.from_dict(geometry_spec_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



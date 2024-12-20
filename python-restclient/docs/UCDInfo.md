# UCDInfo


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**ucd_grid_nodes** | **List[List[List[Coordinate]]]** |  | [optional] 
**ucd_membrane_quads** | **List[List[int]]** |  | [optional] 
**reduced_ucd_grid_nodes_v** | [**List[Coordinate]**](Coordinate.md) |  | [optional] 
**u_cd_grid_nodes** | **List[List[List[Coordinate]]]** |  | [optional] 
**u_cd_membrane_quads** | **List[List[int]]** |  | [optional] 
**num_volume_nodes_x** | **int** |  | [optional] 
**num_volume_nodes_y** | **int** |  | [optional] 
**num_volume_nodes_z** | **int** |  | [optional] 
**num_points_xyz** | **int** |  | [optional] 
**num_volume_nodes_xy** | **int** |  | [optional] 
**num_volume_cells** | **int** |  | [optional] 
**num_membrane_cells** | **int** |  | [optional] 

## Example

```python
from vcell_client.models.ucd_info import UCDInfo

# TODO update the JSON string below
json = "{}"
# create an instance of UCDInfo from a JSON string
ucd_info_instance = UCDInfo.from_json(json)
# print the JSON string representation of the object
print UCDInfo.to_json()

# convert the object into a dict
ucd_info_dict = ucd_info_instance.to_dict()
# create an instance of UCDInfo from a dict
ucd_info_form_dict = ucd_info.from_dict(ucd_info_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



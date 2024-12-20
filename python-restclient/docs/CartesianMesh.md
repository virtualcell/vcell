# CartesianMesh


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**compressed_bytes** | **bytearray** |  | [optional] 
**u_cd_info** | [**UCDInfo**](UCDInfo.md) |  | [optional] 
**geometry_dimension** | **int** |  | [optional] 
**volume_region_map_subvolume** | **Dict[str, int]** |  | [optional] 
**membrane_region_map_subvolumes_in_out** | **Dict[str, object]** |  | [optional] 
**num_membrane_elements** | **int** |  | [optional] 
**num_membrane_regions** | **int** |  | [optional] 
**num_volume_elements** | **int** |  | [optional] 
**num_volume_regions** | **int** |  | [optional] 
**i_size** | [**ISize**](ISize.md) |  | [optional] 
**size_x** | **int** |  | [optional] 
**size_y** | **int** |  | [optional] 
**size_z** | **int** |  | [optional] 
**membrane_connectivity_ok** | **bool** |  | [optional] 
**output_fields** | **List[object]** |  | [optional] 
**chombo_mesh** | **bool** |  | [optional] 

## Example

```python
from vcell_client.models.cartesian_mesh import CartesianMesh

# TODO update the JSON string below
json = "{}"
# create an instance of CartesianMesh from a JSON string
cartesian_mesh_instance = CartesianMesh.from_json(json)
# print the JSON string representation of the object
print CartesianMesh.to_json()

# convert the object into a dict
cartesian_mesh_dict = cartesian_mesh_instance.to_dict()
# create an instance of CartesianMesh from a dict
cartesian_mesh_form_dict = cartesian_mesh.from_dict(cartesian_mesh_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



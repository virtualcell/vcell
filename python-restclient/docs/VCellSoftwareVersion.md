# VCellSoftwareVersion


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**software_version_string** | **str** |  | [optional] 
**vcell_site** | [**VCellSite**](VCellSite.md) |  | [optional] 
**build_number** | **str** |  | [optional] 
**version_number** | **str** |  | [optional] 
**major_version** | **int** |  | [optional] 
**minor_version** | **int** |  | [optional] 
**patch_version** | **int** |  | [optional] 
**build_int** | **int** |  | [optional] 
**site** | [**VCellSite**](VCellSite.md) |  | [optional] 
**description** | **str** |  | [optional] 

## Example

```python
from vcell_client.models.v_cell_software_version import VCellSoftwareVersion

# TODO update the JSON string below
json = "{}"
# create an instance of VCellSoftwareVersion from a JSON string
v_cell_software_version_instance = VCellSoftwareVersion.from_json(json)
# print the JSON string representation of the object
print VCellSoftwareVersion.to_json()

# convert the object into a dict
v_cell_software_version_dict = v_cell_software_version_instance.to_dict()
# create an instance of VCellSoftwareVersion from a dict
v_cell_software_version_form_dict = v_cell_software_version.from_dict(v_cell_software_version_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



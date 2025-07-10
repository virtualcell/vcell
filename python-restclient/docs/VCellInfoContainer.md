# VCellInfoContainer


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**image_infos** | [**List[VCImageSummary]**](VCImageSummary.md) |  | [optional] 
**geometry_info** | [**List[GeometrySummary]**](GeometrySummary.md) |  | [optional] 
**math_model_infos** | [**List[MathModelSummary]**](MathModelSummary.md) |  | [optional] 
**bio_model_infos** | [**List[BioModelSummary]**](BioModelSummary.md) |  | [optional] 

## Example

```python
from vcell_client.models.v_cell_info_container import VCellInfoContainer

# TODO update the JSON string below
json = "{}"
# create an instance of VCellInfoContainer from a JSON string
v_cell_info_container_instance = VCellInfoContainer.from_json(json)
# print the JSON string representation of the object
print VCellInfoContainer.to_json()

# convert the object into a dict
v_cell_info_container_dict = v_cell_info_container_instance.to_dict()
# create an instance of VCellInfoContainer from a dict
v_cell_info_container_form_dict = v_cell_info_container.from_dict(v_cell_info_container_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



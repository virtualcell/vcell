# VCellSummaryContainer


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**image_summaries** | [**List[VCImageSummary]**](VCImageSummary.md) |  | [optional] 
**geometry_summaries** | [**List[GeometrySummary]**](GeometrySummary.md) |  | [optional] 
**math_model_summaries** | [**List[MathModelSummary]**](MathModelSummary.md) |  | [optional] 
**bio_model_summaries** | [**List[BioModelSummary]**](BioModelSummary.md) |  | [optional] 

## Example

```python
from vcell_client.models.v_cell_summary_container import VCellSummaryContainer

# TODO update the JSON string below
json = "{}"
# create an instance of VCellSummaryContainer from a JSON string
v_cell_summary_container_instance = VCellSummaryContainer.from_json(json)
# print the JSON string representation of the object
print VCellSummaryContainer.to_json()

# convert the object into a dict
v_cell_summary_container_dict = v_cell_summary_container_instance.to_dict()
# create an instance of VCellSummaryContainer from a dict
v_cell_summary_container_form_dict = v_cell_summary_container.from_dict(v_cell_summary_container_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



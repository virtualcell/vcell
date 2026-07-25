# VCInfoContainerSummary


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**bio_model_summaries** | [**List[BioModelSummary]**](BioModelSummary.md) |  | [optional] 
**math_model_summaries** | [**List[MathModelSummary]**](MathModelSummary.md) |  | [optional] 
**geometry_summaries** | [**List[GeometrySummary]**](GeometrySummary.md) |  | [optional] 
**vc_image_summaries** | [**List[VCImageSummary]**](VCImageSummary.md) |  | [optional] 

## Example

```python
from vcell_client.models.vc_info_container_summary import VCInfoContainerSummary

# TODO update the JSON string below
json = "{}"
# create an instance of VCInfoContainerSummary from a JSON string
vc_info_container_summary_instance = VCInfoContainerSummary.from_json(json)
# print the JSON string representation of the object
print VCInfoContainerSummary.to_json()

# convert the object into a dict
vc_info_container_summary_dict = vc_info_container_summary_instance.to_dict()
# create an instance of VCInfoContainerSummary from a dict
vc_info_container_summary_form_dict = vc_info_container_summary.from_dict(vc_info_container_summary_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



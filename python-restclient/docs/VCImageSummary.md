# VCImageSummary


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**size** | [**ISize**](ISize.md) |  | [optional] 
**extent** | [**Extent**](Extent.md) |  | [optional] 
**version** | [**Version**](Version.md) |  | [optional] 
**preview** | [**GIFImage**](GIFImage.md) |  | [optional] 
**software_version** | [**VCellSoftwareVersion**](VCellSoftwareVersion.md) |  | [optional] 

## Example

```python
from vcell_client.models.vc_image_summary import VCImageSummary

# TODO update the JSON string below
json = "{}"
# create an instance of VCImageSummary from a JSON string
vc_image_summary_instance = VCImageSummary.from_json(json)
# print the JSON string representation of the object
print VCImageSummary.to_json()

# convert the object into a dict
vc_image_summary_dict = vc_image_summary_instance.to_dict()
# create an instance of VCImageSummary from a dict
vc_image_summary_form_dict = vc_image_summary.from_dict(vc_image_summary_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



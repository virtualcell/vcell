# GeometrySummary


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**dimension** | **int** |  | [optional] 
**origin** | [**Origin**](Origin.md) |  | [optional] 
**extent** | [**Extent**](Extent.md) |  | [optional] 
**image_ref** | **str** |  | [optional] 
**version** | [**Version**](Version.md) |  | [optional] 
**software_version** | [**VCellSoftwareVersion**](VCellSoftwareVersion.md) |  | [optional] 

## Example

```python
from vcell_client.models.geometry_summary import GeometrySummary

# TODO update the JSON string below
json = "{}"
# create an instance of GeometrySummary from a JSON string
geometry_summary_instance = GeometrySummary.from_json(json)
# print the JSON string representation of the object
print GeometrySummary.to_json()

# convert the object into a dict
geometry_summary_dict = geometry_summary_instance.to_dict()
# create an instance of GeometrySummary from a dict
geometry_summary_form_dict = geometry_summary.from_dict(geometry_summary_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



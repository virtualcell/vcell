# BioModelSummary


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**version** | [**Version**](Version.md) |  | [optional] 
**summary** | [**BioModelChildSummary**](BioModelChildSummary.md) |  | [optional] 
**publication_information** | [**List[PublicationInfo]**](PublicationInfo.md) |  | [optional] 
**v_cell_software_version** | [**VCellSoftwareVersion**](VCellSoftwareVersion.md) |  | [optional] 

## Example

```python
from vcell_client.models.bio_model_summary import BioModelSummary

# TODO update the JSON string below
json = "{}"
# create an instance of BioModelSummary from a JSON string
bio_model_summary_instance = BioModelSummary.from_json(json)
# print the JSON string representation of the object
print BioModelSummary.to_json()

# convert the object into a dict
bio_model_summary_dict = bio_model_summary_instance.to_dict()
# create an instance of BioModelSummary from a dict
bio_model_summary_form_dict = bio_model_summary.from_dict(bio_model_summary_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



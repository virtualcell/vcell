# BioModelContext


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**version** | [**Version**](Version.md) |  | [optional] 
**summary** | [**BioModelChildSummary**](BioModelChildSummary.md) |  | [optional] 
**publication_information** | [**List[PublicationInfo]**](PublicationInfo.md) |  | [optional] 
**v_cell_software_version** | [**VCellSoftwareVersion**](VCellSoftwareVersion.md) |  | [optional] 

## Example

```python
from vcell_client.models.bio_model_context import BioModelContext

# TODO update the JSON string below
json = "{}"
# create an instance of BioModelContext from a JSON string
bio_model_context_instance = BioModelContext.from_json(json)
# print the JSON string representation of the object
print BioModelContext.to_json()

# convert the object into a dict
bio_model_context_dict = bio_model_context_instance.to_dict()
# create an instance of BioModelContext from a dict
bio_model_context_form_dict = bio_model_context.from_dict(bio_model_context_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



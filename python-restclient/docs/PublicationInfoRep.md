# PublicationInfoRep


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**publication_key** | **str** |  | [optional] 
**version_key** | **str** |  | [optional] 
**title** | **str** |  | [optional] 
**authors** | **List[str]** |  | [optional] 
**citation** | **str** |  | [optional] 
**pubmedid** | **str** |  | [optional] 
**doi** | **str** |  | [optional] 
**url** | **str** |  | [optional] 
**vc_document_type** | **str** |  | [optional] 
**user** | [**UserRep**](UserRep.md) |  | [optional] 
**pub_date** | **int** |  | [optional] 

## Example

```python
from vcell_client.models.publication_info_rep import PublicationInfoRep

# TODO update the JSON string below
json = "{}"
# create an instance of PublicationInfoRep from a JSON string
publication_info_rep_instance = PublicationInfoRep.from_json(json)
# print the JSON string representation of the object
print PublicationInfoRep.to_json()

# convert the object into a dict
publication_info_rep_dict = publication_info_rep_instance.to_dict()
# create an instance of PublicationInfoRep from a dict
publication_info_rep_form_dict = publication_info_rep.from_dict(publication_info_rep_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



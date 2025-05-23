# PublicationInfo


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**publication_key** | [**KeyValue**](KeyValue.md) |  | [optional] 
**version_key** | [**KeyValue**](KeyValue.md) |  | [optional] 
**title** | **str** |  | [optional] 
**authors** | **List[str]** |  | [optional] 
**citation** | **str** |  | [optional] 
**pubmedid** | **str** |  | [optional] 
**doi** | **str** |  | [optional] 
**url** | **str** |  | [optional] 
**pubdate** | **date** |  | [optional] 
**vc_document_type** | [**VCDocumentType**](VCDocumentType.md) |  | [optional] 
**user** | [**User**](User.md) |  | [optional] 
**the_hash_code** | **int** |  | [optional] 
**pub_date** | **date** |  | [optional] 

## Example

```python
from vcell_client.models.publication_info import PublicationInfo

# TODO update the JSON string below
json = "{}"
# create an instance of PublicationInfo from a JSON string
publication_info_instance = PublicationInfo.from_json(json)
# print the JSON string representation of the object
print PublicationInfo.to_json()

# convert the object into a dict
publication_info_dict = publication_info_instance.to_dict()
# create an instance of PublicationInfo from a dict
publication_info_form_dict = publication_info.from_dict(publication_info_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



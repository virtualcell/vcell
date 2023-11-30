# Publication


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**pub_key** | **int** |  | [optional] 
**title** | **str** |  | [optional] 
**authors** | **List[str]** |  | [optional] 
**year** | **int** |  | [optional] 
**citation** | **str** |  | [optional] 
**pubmedid** | **str** |  | [optional] 
**doi** | **str** |  | [optional] 
**endnoteid** | **int** |  | [optional] 
**url** | **str** |  | [optional] 
**wittid** | **int** |  | [optional] 
**biomodel_refs** | [**List[BiomodelRef]**](BiomodelRef.md) |  | [optional] 
**mathmodel_refs** | [**List[MathmodelRef]**](MathmodelRef.md) |  | [optional] 
**var_date** | **date** |  | [optional] 

## Example

```python
from vcell_client.models.publication import Publication

# TODO update the JSON string below
json = "{}"
# create an instance of Publication from a JSON string
publication_instance = Publication.from_json(json)
# print the JSON string representation of the object
print Publication.to_json()

# convert the object into a dict
publication_dict = publication_instance.to_dict()
# create an instance of Publication from a dict
publication_form_dict = publication.from_dict(publication_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



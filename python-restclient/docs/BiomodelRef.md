# BiomodelRef


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**bm_key** | **int** |  | [optional] 
**name** | **str** |  | [optional] 
**owner_name** | **str** |  | [optional] 
**owner_key** | **int** |  | [optional] 
**version_flag** | **int** |  | [optional] 

## Example

```python
from vcell_client.models.biomodel_ref import BiomodelRef

# TODO update the JSON string below
json = "{}"
# create an instance of BiomodelRef from a JSON string
biomodel_ref_instance = BiomodelRef.from_json(json)
# print the JSON string representation of the object
print BiomodelRef.to_json()

# convert the object into a dict
biomodel_ref_dict = biomodel_ref_instance.to_dict()
# create an instance of BiomodelRef from a dict
biomodel_ref_form_dict = biomodel_ref.from_dict(biomodel_ref_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



# BioModel


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**bm_key** | **str** |  | [optional] 
**name** | **str** |  | [optional] 
**privacy** | **int** |  | [optional] 
**group_users** | **List[str]** |  | [optional] 
**saved_date** | **int** |  | [optional] 
**annot** | **str** |  | [optional] 
**branch_id** | **str** |  | [optional] 
**phys_model_key** | **str** |  | [optional] 
**owner_name** | **str** |  | [optional] 
**owner_key** | **str** |  | [optional] 
**simulation_key_list** | **List[str]** |  | [optional] 
**applications** | **List[object]** |  | [optional] 

## Example

```python
from vcell_client.models.bio_model import BioModel

# TODO update the JSON string below
json = "{}"
# create an instance of BioModel from a JSON string
bio_model_instance = BioModel.from_json(json)
# print the JSON string representation of the object
print BioModel.to_json()

# convert the object into a dict
bio_model_dict = bio_model_instance.to_dict()
# create an instance of BioModel from a dict
bio_model_form_dict = bio_model.from_dict(bio_model_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



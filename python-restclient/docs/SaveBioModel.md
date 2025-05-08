# SaveBioModel


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**bio_model_xml** | **str** |  | 
**name** | **str** |  | [optional] 
**sims_requiring_updates** | **List[str]** |  | [optional] 

## Example

```python
from vcell_client.models.save_bio_model import SaveBioModel

# TODO update the JSON string below
json = "{}"
# create an instance of SaveBioModel from a JSON string
save_bio_model_instance = SaveBioModel.from_json(json)
# print the JSON string representation of the object
print SaveBioModel.to_json()

# convert the object into a dict
save_bio_model_dict = save_bio_model_instance.to_dict()
# create an instance of SaveBioModel from a dict
save_bio_model_form_dict = save_bio_model.from_dict(save_bio_model_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



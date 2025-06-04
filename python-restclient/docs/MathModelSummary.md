# MathModelSummary


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**version** | [**Version**](Version.md) |  | [optional] 
**key_value** | **str** |  | [optional] 
**child_summary** | [**MathModelChildSummary**](MathModelChildSummary.md) |  | [optional] 
**software_version** | [**VCellSoftwareVersion**](VCellSoftwareVersion.md) |  | [optional] 
**publication_infos** | [**List[PublicationInfo]**](PublicationInfo.md) |  | [optional] 
**annotated_functions** | **str** |  | [optional] 

## Example

```python
from vcell_client.models.math_model_summary import MathModelSummary

# TODO update the JSON string below
json = "{}"
# create an instance of MathModelSummary from a JSON string
math_model_summary_instance = MathModelSummary.from_json(json)
# print the JSON string representation of the object
print MathModelSummary.to_json()

# convert the object into a dict
math_model_summary_dict = math_model_summary_instance.to_dict()
# create an instance of MathModelSummary from a dict
math_model_summary_form_dict = math_model_summary.from_dict(math_model_summary_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



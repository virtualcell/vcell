# AnnotatedFunctionDTO


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**function_name** | **str** |  | [optional] 
**function_expression** | **str** |  | [optional] 
**error** | **str** |  | [optional] 
**domain** | [**Domain**](Domain.md) |  | [optional] 
**function_type** | [**VariableType**](VariableType.md) |  | [optional] 
**category** | [**FunctionCategory**](FunctionCategory.md) |  | [optional] 

## Example

```python
from vcell_client.models.annotated_function_dto import AnnotatedFunctionDTO

# TODO update the JSON string below
json = "{}"
# create an instance of AnnotatedFunctionDTO from a JSON string
annotated_function_dto_instance = AnnotatedFunctionDTO.from_json(json)
# print the JSON string representation of the object
print AnnotatedFunctionDTO.to_json()

# convert the object into a dict
annotated_function_dto_dict = annotated_function_dto_instance.to_dict()
# create an instance of AnnotatedFunctionDTO from a dict
annotated_function_dto_form_dict = annotated_function_dto.from_dict(annotated_function_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



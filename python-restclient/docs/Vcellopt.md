# Vcellopt


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**opt_problem** | [**OptProblem**](OptProblem.md) |  | [optional] 
**opt_result_set** | [**OptResultSet**](OptResultSet.md) |  | [optional] 
**status** | [**VcelloptStatus**](VcelloptStatus.md) |  | [optional] 
**status_message** | **str** |  | [optional] 

## Example

```python
from vcell_client.models.vcellopt import Vcellopt

# TODO update the JSON string below
json = "{}"
# create an instance of Vcellopt from a JSON string
vcellopt_instance = Vcellopt.from_json(json)
# print the JSON string representation of the object
print Vcellopt.to_json()

# convert the object into a dict
vcellopt_dict = vcellopt_instance.to_dict()
# create an instance of Vcellopt from a dict
vcellopt_form_dict = vcellopt.from_dict(vcellopt_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)



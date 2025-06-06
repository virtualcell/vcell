# coding: utf-8

"""
    VCell API

    VCell API

    The version of the OpenAPI document: 1.0.1
    Contact: vcell_support@uchc.com
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


from __future__ import annotations
import pprint
import re  # noqa: F401
import json


from typing import Any, ClassVar, Dict, List, Optional, Union
from pydantic import BaseModel, StrictInt, StrictStr
from pydantic import Field
try:
    from typing import Self
except ImportError:
    from typing_extensions import Self

class BioModel(BaseModel):
    """
    BioModel
    """ # noqa: E501
    bm_key: Optional[StrictStr] = Field(default=None, alias="bmKey")
    name: Optional[StrictStr] = None
    privacy: Optional[StrictInt] = None
    group_users: Optional[List[StrictStr]] = Field(default=None, alias="groupUsers")
    saved_date: Optional[StrictInt] = Field(default=None, alias="savedDate")
    annot: Optional[StrictStr] = None
    branch_id: Optional[StrictStr] = Field(default=None, alias="branchID")
    phys_model_key: Optional[StrictStr] = Field(default=None, alias="physModelKey")
    owner_name: Optional[StrictStr] = Field(default=None, alias="ownerName")
    owner_key: Optional[StrictStr] = Field(default=None, alias="ownerKey")
    simulation_key_list: Optional[List[StrictStr]] = Field(default=None, alias="simulationKeyList")
    applications: Optional[List[Union[str, Any]]] = None
    __properties: ClassVar[List[str]] = ["bmKey", "name", "privacy", "groupUsers", "savedDate", "annot", "branchID", "physModelKey", "ownerName", "ownerKey", "simulationKeyList", "applications"]

    model_config = {
        "populate_by_name": True,
        "validate_assignment": True
    }


    def to_str(self) -> str:
        """Returns the string representation of the model using alias"""
        return pprint.pformat(self.model_dump(by_alias=True))

    def to_json(self) -> str:
        """Returns the JSON representation of the model using alias"""
        # TODO: pydantic v2: use .model_dump_json(by_alias=True, exclude_unset=True) instead
        return json.dumps(self.to_dict())

    @classmethod
    def from_json(cls, json_str: str) -> Self:
        """Create an instance of BioModel from a JSON string"""
        return cls.from_dict(json.loads(json_str))

    def to_dict(self) -> Dict[str, Any]:
        """Return the dictionary representation of the model using alias.

        This has the following differences from calling pydantic's
        `self.model_dump(by_alias=True)`:

        * `None` is only added to the output dict for nullable fields that
          were set at model initialization. Other fields with value `None`
          are ignored.
        """
        _dict = self.model_dump(
            by_alias=True,
            exclude={
            },
            exclude_none=True,
        )
        return _dict

    @classmethod
    def from_dict(cls, obj: Dict) -> Self:
        """Create an instance of BioModel from a dict"""
        if obj is None:
            return None

        if not isinstance(obj, dict):
            return cls.model_validate(obj)

        # raise errors for additional fields in the input
        for _key in obj.keys():
            if _key not in cls.__properties:
                raise ValueError("Error due to additional fields (not defined in BioModel) in the input: " + _key)

        _obj = cls.model_validate({
            "bmKey": obj.get("bmKey"),
            "name": obj.get("name"),
            "privacy": obj.get("privacy"),
            "groupUsers": obj.get("groupUsers"),
            "savedDate": obj.get("savedDate"),
            "annot": obj.get("annot"),
            "branchID": obj.get("branchID"),
            "physModelKey": obj.get("physModelKey"),
            "ownerName": obj.get("ownerName"),
            "ownerKey": obj.get("ownerKey"),
            "simulationKeyList": obj.get("simulationKeyList"),
            "applications": obj.get("applications")
        })
        return _obj



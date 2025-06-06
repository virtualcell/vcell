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


from typing import Any, ClassVar, Dict, List, Optional
from pydantic import BaseModel, StrictStr
from pydantic import Field
from vcell_client.models.math_model_child_summary import MathModelChildSummary
from vcell_client.models.publication_info import PublicationInfo
from vcell_client.models.v_cell_software_version import VCellSoftwareVersion
from vcell_client.models.version import Version
try:
    from typing import Self
except ImportError:
    from typing_extensions import Self

class MathModelSummary(BaseModel):
    """
    MathModelSummary
    """ # noqa: E501
    version: Optional[Version] = None
    key_value: Optional[StrictStr] = Field(default=None, alias="keyValue")
    model_info: Optional[MathModelChildSummary] = Field(default=None, alias="modelInfo")
    software_version: Optional[VCellSoftwareVersion] = Field(default=None, alias="softwareVersion")
    publication_infos: Optional[List[PublicationInfo]] = Field(default=None, alias="publicationInfos")
    annotated_functions: Optional[StrictStr] = Field(default=None, alias="annotatedFunctions")
    __properties: ClassVar[List[str]] = ["version", "keyValue", "modelInfo", "softwareVersion", "publicationInfos", "annotatedFunctions"]

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
        """Create an instance of MathModelSummary from a JSON string"""
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
        # override the default output from pydantic by calling `to_dict()` of version
        if self.version:
            _dict['version'] = self.version.to_dict()
        # override the default output from pydantic by calling `to_dict()` of model_info
        if self.model_info:
            _dict['modelInfo'] = self.model_info.to_dict()
        # override the default output from pydantic by calling `to_dict()` of software_version
        if self.software_version:
            _dict['softwareVersion'] = self.software_version.to_dict()
        # override the default output from pydantic by calling `to_dict()` of each item in publication_infos (list)
        _items = []
        if self.publication_infos:
            for _item in self.publication_infos:
                if _item:
                    _items.append(_item.to_dict())
            _dict['publicationInfos'] = _items
        return _dict

    @classmethod
    def from_dict(cls, obj: Dict) -> Self:
        """Create an instance of MathModelSummary from a dict"""
        if obj is None:
            return None

        if not isinstance(obj, dict):
            return cls.model_validate(obj)

        # raise errors for additional fields in the input
        for _key in obj.keys():
            if _key not in cls.__properties:
                raise ValueError("Error due to additional fields (not defined in MathModelSummary) in the input: " + _key)

        _obj = cls.model_validate({
            "version": Version.from_dict(obj.get("version")) if obj.get("version") is not None else None,
            "keyValue": obj.get("keyValue"),
            "modelInfo": MathModelChildSummary.from_dict(obj.get("modelInfo")) if obj.get("modelInfo") is not None else None,
            "softwareVersion": VCellSoftwareVersion.from_dict(obj.get("softwareVersion")) if obj.get("softwareVersion") is not None else None,
            "publicationInfos": [PublicationInfo.from_dict(_item) for _item in obj.get("publicationInfos")] if obj.get("publicationInfos") is not None else None,
            "annotatedFunctions": obj.get("annotatedFunctions")
        })
        return _obj



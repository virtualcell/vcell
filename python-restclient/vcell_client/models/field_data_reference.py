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
from vcell_client.models.external_data_identifier import ExternalDataIdentifier
from vcell_client.models.key_value import KeyValue
try:
    from typing import Self
except ImportError:
    from typing_extensions import Self

class FieldDataReference(BaseModel):
    """
    FieldDataReference
    """ # noqa: E501
    external_data_identifier: Optional[ExternalDataIdentifier] = Field(default=None, alias="externalDataIdentifier")
    external_data_annotation: Optional[StrictStr] = Field(default=None, alias="externalDataAnnotation")
    external_data_id_sim_refs: Optional[List[KeyValue]] = Field(default=None, alias="externalDataIDSimRefs")
    __properties: ClassVar[List[str]] = ["externalDataIdentifier", "externalDataAnnotation", "externalDataIDSimRefs"]

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
        """Create an instance of FieldDataReference from a JSON string"""
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
        # override the default output from pydantic by calling `to_dict()` of external_data_identifier
        if self.external_data_identifier:
            _dict['externalDataIdentifier'] = self.external_data_identifier.to_dict()
        # override the default output from pydantic by calling `to_dict()` of each item in external_data_id_sim_refs (list)
        _items = []
        if self.external_data_id_sim_refs:
            for _item in self.external_data_id_sim_refs:
                if _item:
                    _items.append(_item.to_dict())
            _dict['externalDataIDSimRefs'] = _items
        return _dict

    @classmethod
    def from_dict(cls, obj: Dict) -> Self:
        """Create an instance of FieldDataReference from a dict"""
        if obj is None:
            return None

        if not isinstance(obj, dict):
            return cls.model_validate(obj)

        # raise errors for additional fields in the input
        for _key in obj.keys():
            if _key not in cls.__properties:
                raise ValueError("Error due to additional fields (not defined in FieldDataReference) in the input: " + _key)

        _obj = cls.model_validate({
            "externalDataIdentifier": ExternalDataIdentifier.from_dict(obj.get("externalDataIdentifier")) if obj.get("externalDataIdentifier") is not None else None,
            "externalDataAnnotation": obj.get("externalDataAnnotation"),
            "externalDataIDSimRefs": [KeyValue.from_dict(_item) for _item in obj.get("externalDataIDSimRefs")] if obj.get("externalDataIDSimRefs") is not None else None
        })
        return _obj



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
import json
import pprint
import re  # noqa: F401
from enum import Enum



try:
    from typing import Self
except ImportError:
    from typing_extensions import Self


class Status(str, Enum):
    """
    Status
    """

    """
    allowed enum values
    """
    UNKNOWN = 'UNKNOWN'
    NEVER_RAN = 'NEVER_RAN'
    START_REQUESTED = 'START_REQUESTED'
    DISPATCHED = 'DISPATCHED'
    WAITING = 'WAITING'
    QUEUED = 'QUEUED'
    RUNNING = 'RUNNING'
    COMPLETED = 'COMPLETED'
    FAILED = 'FAILED'
    STOP_REQUESTED = 'STOP_REQUESTED'
    STOPPED = 'STOPPED'
    NOT_SAVED = 'NOT_SAVED'

    @classmethod
    def from_json(cls, json_str: str) -> Self:
        """Create an instance of Status from a JSON string"""
        return cls(json.loads(json_str))


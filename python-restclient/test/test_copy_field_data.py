# coding: utf-8

"""
    VCell API

    VCell API

    The version of the OpenAPI document: 1.0.1
    Contact: vcell_support@uchc.com
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


import unittest
import datetime

from vcell_client.models.copy_field_data import CopyFieldData

class TestCopyFieldData(unittest.TestCase):
    """CopyFieldData unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> CopyFieldData:
        """Test CopyFieldData
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `CopyFieldData`
        """
        model = CopyFieldData()
        if include_optional:
            return CopyFieldData(
                model_id = '',
                model_type = 'BIOMODEL'
            )
        else:
            return CopyFieldData(
        )
        """

    def testCopyFieldData(self):
        """Test CopyFieldData"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()

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

from vcell_client.models.field_data_saved_results import FieldDataSavedResults

class TestFieldDataSavedResults(unittest.TestCase):
    """FieldDataSavedResults unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> FieldDataSavedResults:
        """Test FieldDataSavedResults
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `FieldDataSavedResults`
        """
        model = FieldDataSavedResults()
        if include_optional:
            return FieldDataSavedResults(
                field_data_name = '',
                field_data_key = ''
            )
        else:
            return FieldDataSavedResults(
        )
        """

    def testFieldDataSavedResults(self):
        """Test FieldDataSavedResults"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()

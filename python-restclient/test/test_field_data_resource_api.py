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

from vcell_client.api.field_data_resource_api import FieldDataResourceApi


class TestFieldDataResourceApi(unittest.TestCase):
    """FieldDataResourceApi unit test stubs"""

    def setUp(self) -> None:
        self.api = FieldDataResourceApi()

    def tearDown(self) -> None:
        pass

    def test_analyze_field_data_file(self) -> None:
        """Test case for analyze_field_data_file

        Analyze the field data from the uploaded file. Filenames must be lowercase alphanumeric, and can contain underscores.
        """
        pass

    def test_create_field_data_from_analyzed_file(self) -> None:
        """Test case for create_field_data_from_analyzed_file

        Take the analyzed results of the field data, modify it to your liking, then save it on the server.
        """
        pass

    def test_delete_field_data(self) -> None:
        """Test case for delete_field_data

        Delete the selected field data.
        """
        pass

    def test_get_all_field_data_ids(self) -> None:
        """Test case for get_all_field_data_ids

        Get all of the ids used to identify, and retrieve field data.
        """
        pass

    def test_get_field_data_shape_from_id(self) -> None:
        """Test case for get_field_data_shape_from_id

        Get the shape of the field data. That is it's size, origin, extent, and data identifiers.
        """
        pass


if __name__ == '__main__':
    unittest.main()
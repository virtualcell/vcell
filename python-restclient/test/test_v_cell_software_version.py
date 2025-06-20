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

from vcell_client.models.v_cell_software_version import VCellSoftwareVersion

class TestVCellSoftwareVersion(unittest.TestCase):
    """VCellSoftwareVersion unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> VCellSoftwareVersion:
        """Test VCellSoftwareVersion
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `VCellSoftwareVersion`
        """
        model = VCellSoftwareVersion()
        if include_optional:
            return VCellSoftwareVersion(
                software_version_string = '',
                vcell_site = 'alpha',
                build_number = '',
                version_number = '',
                major_version = 56,
                minor_version = 56,
                patch_version = 56,
                build_int = 56,
                description = ''
            )
        else:
            return VCellSoftwareVersion(
        )
        """

    def testVCellSoftwareVersion(self):
        """Test VCellSoftwareVersion"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()

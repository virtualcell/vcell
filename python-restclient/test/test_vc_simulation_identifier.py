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

from vcell_client.models.vc_simulation_identifier import VCSimulationIdentifier

class TestVCSimulationIdentifier(unittest.TestCase):
    """VCSimulationIdentifier unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> VCSimulationIdentifier:
        """Test VCSimulationIdentifier
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `VCSimulationIdentifier`
        """
        model = VCSimulationIdentifier()
        if include_optional:
            return VCSimulationIdentifier(
                simulation_key = vcell_client.models.key_value.KeyValue(
                    value = 1.337, ),
                owner = vcell_client.models.user.User(
                    user_name = '', 
                    key = vcell_client.models.key_value.KeyValue(
                        value = 1.337, ), 
                    i_d = vcell_client.models.key_value.KeyValue(
                        value = 1.337, ), 
                    name = '', 
                    test_account = True, ),
                i_d = ''
            )
        else:
            return VCSimulationIdentifier(
        )
        """

    def testVCSimulationIdentifier(self):
        """Test VCSimulationIdentifier"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()
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

from vcell_client.models.gif_image import GIFImage

class TestGIFImage(unittest.TestCase):
    """GIFImage unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> GIFImage:
        """Test GIFImage
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `GIFImage`
        """
        model = GIFImage()
        if include_optional:
            return GIFImage(
                gif_encoded_data = bytes(b'blah'),
                size = vcell_client.models.i_size.ISize(
                    x = 56, 
                    y = 56, 
                    z = 56, )
            )
        else:
            return GIFImage(
        )
        """

    def testGIFImage(self):
        """Test GIFImage"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()

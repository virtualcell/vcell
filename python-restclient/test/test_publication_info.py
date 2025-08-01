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

from vcell_client.models.publication_info import PublicationInfo

class TestPublicationInfo(unittest.TestCase):
    """PublicationInfo unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> PublicationInfo:
        """Test PublicationInfo
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `PublicationInfo`
        """
        model = PublicationInfo()
        if include_optional:
            return PublicationInfo(
                publication_key = '',
                version_key = '',
                title = '',
                authors = [
                    ''
                    ],
                citation = '',
                pubmedid = '',
                doi = '',
                url = '',
                pubdate = 'Thu Mar 10 00:00:00 UTC 2022',
                vc_document_type = 'BIOMODEL_DOC',
                user = vcell_client.models.user.User(
                    user_name = '', 
                    key = '', 
                    my_specials = [
                        'admins'
                        ], ),
                the_hash_code = 56
            )
        else:
            return PublicationInfo(
        )
        """

    def testPublicationInfo(self):
        """Test PublicationInfo"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()

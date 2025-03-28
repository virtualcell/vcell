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

from vcell_client.api.simulation_resource_api import SimulationResourceApi


class TestSimulationResourceApi(unittest.TestCase):
    """SimulationResourceApi unit test stubs"""

    def setUp(self) -> None:
        self.api = SimulationResourceApi()

    def tearDown(self) -> None:
        pass

    def test_get_simulation_status(self) -> None:
        """Test case for get_simulation_status

        Get the status of simulation running
        """
        pass

    def test_start_simulation(self) -> None:
        """Test case for start_simulation

        Start a simulation.
        """
        pass

    def test_stop_simulation(self) -> None:
        """Test case for stop_simulation

        Stop a simulation.
        """
        pass


if __name__ == '__main__':
    unittest.main()

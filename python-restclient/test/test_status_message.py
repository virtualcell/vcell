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

from vcell_client.models.status_message import StatusMessage

class TestStatusMessage(unittest.TestCase):
    """StatusMessage unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> StatusMessage:
        """Test StatusMessage
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `StatusMessage`
        """
        model = StatusMessage()
        if include_optional:
            return StatusMessage(
                job_status = vcell_client.models.simulation_job_status.SimulationJobStatus(
                    field_time_date_stamp = 'Thu Mar 10 00:00:00 UTC 2022', 
                    field_vc_sim_id = vcell_client.models.vc_simulation_identifier.VCSimulationIdentifier(
                        simulation_key = vcell_client.models.key_value.KeyValue(
                            value = 1.337, ), 
                        owner = vcell_client.models.user.User(
                            user_name = '', 
                            key = vcell_client.models.key_value.KeyValue(
                                value = 1.337, ), 
                            i_d = , 
                            name = '', 
                            test_account = True, ), 
                        i_d = '', ), 
                    field_submit_date = 'Thu Mar 10 00:00:00 UTC 2022', 
                    field_scheduler_status = 'WAITING', 
                    field_task_id = 56, 
                    field_simulation_message = vcell_client.models.simulation_message.SimulationMessage(
                        detailed_state = 'UNKNOWN', 
                        message = '', 
                        htc_job_id = vcell_client.models.htc_job_id.HtcJobID(
                            job_number = 56, 
                            server = '', 
                            batch_system_type = 'PBS', ), 
                        display_message = '', ), 
                    field_server_id = vcell_client.models.v_cell_server_id.VCellServerID(
                        server_id = '', ), 
                    field_job_index = 56, 
                    field_simulation_queue_entry_status = vcell_client.models.simulation_queue_entry_status.SimulationQueueEntryStatus(
                        field_queue_priority = 56, 
                        field_queue_date = 'Thu Mar 10 00:00:00 UTC 2022', 
                        field_queue_id = 'QUEUE_ID_WAITING', 
                        queue_date = 'Thu Mar 10 00:00:00 UTC 2022', 
                        queue_id = 'QUEUE_ID_WAITING', 
                        queue_priority = 56, ), 
                    field_simulation_execution_status = vcell_client.models.simulation_execution_status.SimulationExecutionStatus(
                        field_start_date = 'Thu Mar 10 00:00:00 UTC 2022', 
                        field_latest_update_date = 'Thu Mar 10 00:00:00 UTC 2022', 
                        field_end_date = 'Thu Mar 10 00:00:00 UTC 2022', 
                        field_compute_host = '', 
                        field_has_data = True, 
                        field_htc_job_id = vcell_client.models.htc_job_id.HtcJobID(
                            job_number = 56, 
                            server = '', ), 
                        compute_host = '', 
                        end_date = 'Thu Mar 10 00:00:00 UTC 2022', 
                        latest_update_date = 'Thu Mar 10 00:00:00 UTC 2022', 
                        start_date = 'Thu Mar 10 00:00:00 UTC 2022', 
                        htc_job_id = , ), 
                    compute_host = '', 
                    end_date = 'Thu Mar 10 00:00:00 UTC 2022', 
                    job_index = 56, 
                    scheduler_status = 'WAITING', 
                    server_id = vcell_client.models.v_cell_server_id.VCellServerID(), 
                    simulation_execution_status = vcell_client.models.simulation_execution_status.SimulationExecutionStatus(
                        field_start_date = 'Thu Mar 10 00:00:00 UTC 2022', 
                        field_latest_update_date = 'Thu Mar 10 00:00:00 UTC 2022', 
                        field_end_date = 'Thu Mar 10 00:00:00 UTC 2022', 
                        field_compute_host = '', 
                        field_has_data = True, 
                        compute_host = '', 
                        end_date = 'Thu Mar 10 00:00:00 UTC 2022', 
                        latest_update_date = 'Thu Mar 10 00:00:00 UTC 2022', 
                        start_date = 'Thu Mar 10 00:00:00 UTC 2022', ), 
                    simulation_queue_entry_status = vcell_client.models.simulation_queue_entry_status.SimulationQueueEntryStatus(
                        field_queue_priority = 56, 
                        field_queue_date = 'Thu Mar 10 00:00:00 UTC 2022', 
                        queue_date = 'Thu Mar 10 00:00:00 UTC 2022', 
                        queue_priority = 56, ), 
                    start_date = 'Thu Mar 10 00:00:00 UTC 2022', 
                    simulation_message = vcell_client.models.simulation_message.SimulationMessage(
                        message = '', 
                        display_message = '', ), 
                    submit_date = 'Thu Mar 10 00:00:00 UTC 2022', 
                    task_id = 56, 
                    time_date_stamp = 'Thu Mar 10 00:00:00 UTC 2022', 
                    v_c_simulation_identifier = vcell_client.models.vc_simulation_identifier.VCSimulationIdentifier(), ),
                user_name = '',
                progress = 1.337,
                timepoint = 1.337
            )
        else:
            return StatusMessage(
        )
        """

    def testStatusMessage(self):
        """Test StatusMessage"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()
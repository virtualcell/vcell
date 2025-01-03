/**
 * VCell API
 * VCell API
 *
 * The version of the OpenAPI document: 1.0.1
 * Contact: vcell_support@uchc.com
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { HtcJobID } from './htc-job-id';


export interface SimulationExecutionStatus { 
    fieldStartDate?: string;
    fieldLatestUpdateDate?: string;
    fieldEndDate?: string;
    fieldComputeHost?: string;
    fieldHasData?: boolean;
    fieldHtcJobID?: HtcJobID;
    computeHost?: string;
    endDate?: string;
    latestUpdateDate?: string;
    startDate?: string;
    htcJobID?: HtcJobID;
}


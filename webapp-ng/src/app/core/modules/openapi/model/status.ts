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


export type Status = 'UNKNOWN' | 'NEVER_RAN' | 'START_REQUESTED' | 'DISPATCHED' | 'WAITING' | 'QUEUED' | 'RUNNING' | 'COMPLETED' | 'FAILED' | 'STOP_REQUESTED' | 'STOPPED' | 'NOT_SAVED';

export const Status = {
    Unknown: 'UNKNOWN' as Status,
    NeverRan: 'NEVER_RAN' as Status,
    StartRequested: 'START_REQUESTED' as Status,
    Dispatched: 'DISPATCHED' as Status,
    Waiting: 'WAITING' as Status,
    Queued: 'QUEUED' as Status,
    Running: 'RUNNING' as Status,
    Completed: 'COMPLETED' as Status,
    Failed: 'FAILED' as Status,
    StopRequested: 'STOP_REQUESTED' as Status,
    Stopped: 'STOPPED' as Status,
    NotSaved: 'NOT_SAVED' as Status
};

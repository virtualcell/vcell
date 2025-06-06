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
import { HttpHeaders }                                       from '@angular/common/http';

import { Observable }                                        from 'rxjs';

import { SimulationStatusPersistentRecord } from '../model/models';
import { StatusMessage } from '../model/models';
import { VCellHTTPError } from '../model/models';


import { Configuration }                                     from '../configuration';



export interface SimulationResourceServiceInterface {
    defaultHeaders: HttpHeaders;
    configuration: Configuration;

    /**
     * Get the status of simulation running
     * 
     * @param simID 
     * @param bioModelID 
     * @param mathModelID 
     */
    getSimulationStatus(simID: string, bioModelID?: string, mathModelID?: string, extraHttpRequestParams?: any): Observable<SimulationStatusPersistentRecord>;

    /**
     * Start a simulation.
     * 
     * @param simID 
     */
    startSimulation(simID: string, extraHttpRequestParams?: any): Observable<Array<StatusMessage>>;

    /**
     * Stop a simulation.
     * 
     * @param simID 
     */
    stopSimulation(simID: string, extraHttpRequestParams?: any): Observable<Array<StatusMessage>>;

}

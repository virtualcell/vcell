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

import { AnalyzedResultsFromFieldData } from '../model/models';
import { FieldDataReference } from '../model/models';
import { FieldDataSaveResults } from '../model/models';
import { FieldDataShape } from '../model/models';


import { Configuration }                                     from '../configuration';



export interface FieldDataResourceServiceInterface {
    defaultHeaders: HttpHeaders;
    configuration: Configuration;

    /**
     * Analyze the field data from the uploaded file. Filenames must be lowercase alphanumeric, and can contain underscores.
     * 
     * @param file 
     * @param fileName 
     */
    analyzeFieldDataFile(file?: Blob, fileName?: string, extraHttpRequestParams?: any): Observable<AnalyzedResultsFromFieldData>;

    /**
     * Take the analyzed results of the field data, modify it to your liking, then save it on the server.
     * 
     * @param analyzedResultsFromFieldData 
     */
    createFieldDataFromAnalyzedFile(analyzedResultsFromFieldData?: AnalyzedResultsFromFieldData, extraHttpRequestParams?: any): Observable<FieldDataSaveResults>;

    /**
     * Create new field data from a simulation.
     * 
     * @param simKeyReference 
     * @param jobIndex 
     * @param newFieldDataName 
     */
    createNewFieldDataFromSimulation(simKeyReference?: string, jobIndex?: number, newFieldDataName?: string, extraHttpRequestParams?: any): Observable<{}>;

    /**
     * Delete the selected field data.
     * 
     * @param fieldDataID 
     */
    deleteFieldData(fieldDataID: string, extraHttpRequestParams?: any): Observable<{}>;

    /**
     * Get all of the ids used to identify, and retrieve field data.
     * 
     */
    getAllFieldDataIDs(extraHttpRequestParams?: any): Observable<Array<FieldDataReference>>;

    /**
     * Get the shape of the field data. That is it\&#39;s size, origin, extent, and data identifiers.
     * 
     * @param fieldDataID 
     */
    getFieldDataShapeFromID(fieldDataID: string, extraHttpRequestParams?: any): Observable<FieldDataShape>;

}

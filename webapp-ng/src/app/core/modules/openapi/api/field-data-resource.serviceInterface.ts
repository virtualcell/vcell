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

import { AnalyzedFile } from '../model/models';
import { Extent } from '../model/models';
import { ExternalDataIdentifier } from '../model/models';
import { FieldDataReference } from '../model/models';
import { FieldDataSavedResults } from '../model/models';
import { FieldDataShape } from '../model/models';
import { ISize } from '../model/models';
import { Origin } from '../model/models';
import { SourceModel } from '../model/models';


import { Configuration }                                     from '../configuration';



export interface FieldDataResourceServiceInterface {
    defaultHeaders: HttpHeaders;
    configuration: Configuration;

    /**
     * Delete the selected field data.
     * 
     * @param fieldDataID 
     */
    _delete(fieldDataID: string, extraHttpRequestParams?: any): Observable<{}>;

    /**
     * Analyze uploaded image file (Tiff, Zip, and Non-GPL BioFormats) and create default field data specification. Color mapped images not supported (the colors in those images will be interpreted as separate channels). Filenames must be lowercase alphanumeric, and can contain underscores.
     * 
     * @param file 
     * @param fileName 
     */
    analyzeFile(file?: Blob, fileName?: string, extraHttpRequestParams?: any): Observable<AnalyzedFile>;

    /**
     * For advanced users, combines the two separate requests of Analyze File and Create From Analyzed File. The following files are accepted: .tif and .zip.
     * 
     * @param file 
     * @param fileName 
     * @param extent 
     * @param iSize 
     * @param channelNames 
     * @param times 
     * @param annotation 
     * @param origin 
     */
    analyzeFileAndCreate(file?: Blob, fileName?: string, extent?: Extent, iSize?: ISize, channelNames?: Array<string>, times?: Array<number>, annotation?: string, origin?: Origin, extraHttpRequestParams?: any): Observable<FieldDataSavedResults>;

    /**
     * Copy all existing field data from a BioModel/MathModel that you have access to, but don\&#39;t own.
     * 
     * @param sourceModel 
     */
    copyModelsFieldData(sourceModel?: SourceModel, extraHttpRequestParams?: any): Observable<{ [key: string]: ExternalDataIdentifier; }>;

    /**
     * Take the field data specification, and save it to the server. User may adjust the analyzed file before uploading to edit defaults.
     * 
     * @param analyzedFile 
     */
    createFromAnalyzedFile(analyzedFile?: AnalyzedFile, extraHttpRequestParams?: any): Observable<FieldDataSavedResults>;

    /**
     * Create new field data from existing simulation results.
     * 
     * @param simKeyReference 
     * @param jobIndex 
     * @param newFieldDataName 
     */
    createFromSimulation(simKeyReference?: string, jobIndex?: number, newFieldDataName?: string, extraHttpRequestParams?: any): Observable<{}>;

    /**
     * Get all of the ids used to identify, and retrieve field data.
     * 
     */
    getAllIDs(extraHttpRequestParams?: any): Observable<Array<FieldDataReference>>;

    /**
     * Get the shape of the field data. That is it\&#39;s size, origin, extent, times, and data identifiers.
     * 
     * @param fieldDataID 
     */
    getShapeFromID(fieldDataID: string, extraHttpRequestParams?: any): Observable<FieldDataShape>;

}

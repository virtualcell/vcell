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

import { VCImageSummary } from '../model/models';
import { VCellHTTPError } from '../model/models';


import { Configuration }                                     from '../configuration';



export interface VCImageResourceServiceInterface {
    defaultHeaders: HttpHeaders;
    configuration: Configuration;

    /**
     * 
     * Remove specific image VCML.
     * @param id 
     */
    deleteImageVCML(id: string, extraHttpRequestParams?: any): Observable<{}>;

    /**
     * 
     * Return Image summaries.
     * @param includePublicAndShared Include Image summaries that are public and shared with the requester. Default is true.
     */
    getImageSummaries(includePublicAndShared?: boolean, extraHttpRequestParams?: any): Observable<Array<VCImageSummary>>;

    /**
     * 
     * All of the miscellaneous information about an Image (Extent, ISize, preview, etc...), but not the actual Image itself.
     * @param id 
     */
    getImageSummary(id: string, extraHttpRequestParams?: any): Observable<VCImageSummary>;

    /**
     * 
     * Get specific image VCML.
     * @param id 
     */
    getImageVCML(id: string, extraHttpRequestParams?: any): Observable<string>;

    /**
     * 
     * Save the VCML representation of an image.
     * @param body 
     * @param name Name to save new ImageVCML under. Leave blank if re-saving existing ImageVCML.
     */
    saveImageVCML(body: string, name?: string, extraHttpRequestParams?: any): Observable<string>;

}

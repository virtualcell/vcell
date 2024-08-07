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
import { MathmodelRef } from './mathmodel-ref';
import { BiomodelRef } from './biomodel-ref';


export interface Publication { 
    pubKey?: number;
    title?: string;
    authors?: Array<string>;
    year?: number;
    citation?: string;
    pubmedid?: string;
    doi?: string;
    endnoteid?: number;
    url?: string;
    wittid?: number;
    biomodelRefs?: Array<BiomodelRef>;
    mathmodelRefs?: Array<MathmodelRef>;
    date?: string;
}


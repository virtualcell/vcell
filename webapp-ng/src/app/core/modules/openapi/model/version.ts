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
import { VersionFlag } from './version-flag';
import { User } from './user';
import { GroupAccess } from './group-access';


export interface Version { 
    versionKey?: string;
    annot?: string;
    branchID?: number;
    branchPointRefKey?: string;
    date?: string;
    flag?: VersionFlag;
    groupAccess?: GroupAccess;
    name?: string;
    owner?: User;
}


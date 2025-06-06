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
import { User } from './user';
import { GroupAccess } from './group-access';


export interface GroupAccessSome extends GroupAccess { 
    type: string;
    hash?: number;
    groupMembers?: Array<User>;
    hiddenMembers?: Array<boolean>;
    description?: string;
    hiddenGroupMembers?: Array<User>;
    normalGroupMembers?: Array<User>;
}


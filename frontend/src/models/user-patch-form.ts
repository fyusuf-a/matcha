/* tslint:disable */
/* eslint-disable */
/**
 * Matcha
 * No description provided (generated by Swagger Codegen https://github.com/swagger-api/swagger-codegen)
 *
 * OpenAPI spec version: 1.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */
import type { Gender } from './gender';
import type { SexualOrientation } from './sexual-orientation';
/**
 * 
 * @export
 * @interface UserPatchForm
 */
export interface UserPatchForm {
    /**
     * 
     * @type {string}
     * @memberof UserPatchForm
     */
    firstName?: string;
    /**
     * 
     * @type {string}
     * @memberof UserPatchForm
     */
    lastName?: string;
    /**
     * 
     * @type {string}
     * @memberof UserPatchForm
     */
    biography?: string;
    /**
     * 
     * @type {Gender}
     * @memberof UserPatchForm
     */
    gender?: Gender;
    /**
     * 
     * @type {SexualOrientation}
     * @memberof UserPatchForm
     */
    sexualOrientation?: SexualOrientation;
}
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
/**
 * 
 * @export
 * @interface LocationDto
 */
export interface LocationDto {
    /**
     * 
     * @type {boolean}
     * @memberof LocationDto
     */
    shared?: boolean;
    /**
     * 
     * @type {number}
     * @memberof LocationDto
     */
    latitude?: number | null;
    /**
     * 
     * @type {number}
     * @memberof LocationDto
     */
    longitude?: number | null;
    /**
     * 
     * @type {Date}
     * @memberof LocationDto
     */
    updatedAt?: Date;
}
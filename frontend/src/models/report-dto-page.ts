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
import type { ReportDto } from './report-dto';
/**
 * 
 * @export
 * @interface ReportDtoPage
 */
export interface ReportDtoPage {
    /**
     * 
     * @type {Array<ReportDto>}
     * @memberof ReportDtoPage
     */
    content?: Array<ReportDto>;
    /**
     * 
     * @type {number}
     * @memberof ReportDtoPage
     */
    totalElements?: number;
    /**
     * 
     * @type {boolean}
     * @memberof ReportDtoPage
     */
    first?: boolean;
    /**
     * 
     * @type {boolean}
     * @memberof ReportDtoPage
     */
    last?: boolean;
    /**
     * 
     * @type {number}
     * @memberof ReportDtoPage
     */
    pageSize?: number;
    /**
     * 
     * @type {number}
     * @memberof ReportDtoPage
     */
    totalPages?: number;
    /**
     * 
     * @type {number}
     * @memberof ReportDtoPage
     */
    pageNumber?: number;
}

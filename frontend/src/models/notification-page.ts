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
import type { Notification } from './notification';
/**
 * 
 * @export
 * @interface NotificationPage
 */
export interface NotificationPage {
    /**
     * 
     * @type {Array<Notification>}
     * @memberof NotificationPage
     */
    content?: Array<Notification>;
    /**
     * 
     * @type {number}
     * @memberof NotificationPage
     */
    totalElements?: number;
    /**
     * 
     * @type {boolean}
     * @memberof NotificationPage
     */
    first?: boolean;
    /**
     * 
     * @type {boolean}
     * @memberof NotificationPage
     */
    last?: boolean;
    /**
     * 
     * @type {number}
     * @memberof NotificationPage
     */
    pageSize?: number;
    /**
     * 
     * @type {number}
     * @memberof NotificationPage
     */
    totalPages?: number;
    /**
     * 
     * @type {number}
     * @memberof NotificationPage
     */
    pageNumber?: number;
}

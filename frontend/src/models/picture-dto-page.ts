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
import type { PictureDto } from './picture-dto';
/**
 * 
 * @export
 * @interface PictureDtoPage
 */
export interface PictureDtoPage {
    /**
     * 
     * @type {Array<PictureDto>}
     * @memberof PictureDtoPage
     */
    content?: Array<PictureDto>;
    /**
     * 
     * @type {number}
     * @memberof PictureDtoPage
     */
    totalElements?: number;
    /**
     * 
     * @type {boolean}
     * @memberof PictureDtoPage
     */
    first?: boolean;
    /**
     * 
     * @type {boolean}
     * @memberof PictureDtoPage
     */
    last?: boolean;
    /**
     * 
     * @type {number}
     * @memberof PictureDtoPage
     */
    pageSize?: number;
    /**
     * 
     * @type {number}
     * @memberof PictureDtoPage
     */
    totalPages?: number;
    /**
     * 
     * @type {number}
     * @memberof PictureDtoPage
     */
    pageNumber?: number;
}

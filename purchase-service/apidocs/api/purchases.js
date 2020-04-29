/**
 * @apiDefine NoContent
 *
 *  @apiSuccessExample {json} Success 204 response example:
 *  HTTP/1.1 204 No Content
 */

/**
 * @apiDefine GetValidPurchasesSuccess200Return
 *
 * @apiSuccess {Long} 	        id 				                id of the purchase.
 * @apiSuccess {String} 	    productType  	                product type.
 * @apiSuccess {Date} 		    expires  	                    expiration date.
 * @apiSuccess {Object[]} 		purchaseDetails  	            list of purchaseDetails.
 * @apiSuccess {Long} 	        purchaseDetails.id 				id of the item.
 * @apiSuccess {String} 	    purchaseDetails.description  	description of the item.
 * @apiSuccess {Integer} 	    purchaseDetails.quantity  	    quantity.
 * @apiSuccess {Double} 	    purchaseDetails.value  	        total value.
 *
 * @apiSuccessExample {json} Success 200 response example:
 *  HTTP/1.1 200 OK
 *  Content-Type: application/json;charset=UTF-8
 *  Content-Length: 152
 *
 *  [{"id":1,"productType":"Cake","expires":1480496691580,"purchaseDetails":[]},{"id":2,"productType":"Tools","expires":1480496691580,"purchaseDetails":[]}]
 */

/**
 * @apiDefine SavePurchaseSuccess200Return
 *
 * @apiSuccess {Long} 	        id 				                id of the purchase.
 * @apiSuccess {String} 	    productType  	                product type.
 * @apiSuccess {Date} 		    expires  	                    expiration date.
 * @apiSuccess {Object[]} 		purchaseDetails  	            list of purchaseDetails.
 * @apiSuccess {Long} 	        purchaseDetails.id 				id of the item.
 * @apiSuccess {String} 	    purchaseDetails.description  	description of the item.
 * @apiSuccess {Integer} 	    purchaseDetails.quantity  	    quantity.
 * @apiSuccess {Double} 	    purchaseDetails.value  	        total value.
 *
 * @apiSuccessExample {json} Success 201 response example:
 *  HTTP/1.1 201 Created
 *  Content-Type: application/json;charset=UTF-8
 *  Content-Length: 142
 *
 *  {"id":1,"productType":"Cake","expires":1480507595369,"purchaseDetails":[{"id":1,"description":"XXL wedding cake","quantity":1,"value":500.0}]}
 */

/**
 * @apiDefine UpdatePurchaseSuccess200Return
 *
 * @apiSuccess {Long} 	        id 				                id of the purchase.
 * @apiSuccess {String} 	    productType  	                product type.
 * @apiSuccess {Date} 		    expires  	                    expiration date.
 * @apiSuccess {Object[]} 		purchaseDetails  	            list of purchaseDetails.
 * @apiSuccess {Long} 	        purchaseDetails.id 				id of the item.
 * @apiSuccess {String} 	    purchaseDetails.description  	description of the item.
 * @apiSuccess {Integer} 	    purchaseDetails.quantity  	    quantity.
 * @apiSuccess {Double} 	    purchaseDetails.value  	        total value.
 *
 * @apiSuccessExample {json} Success 200 response example:
 *  HTTP/1.1 200 OK
 *  Content-Type: application/json;charset=UTF-8
 *  Content-Length: 141
 *
 *  {"id":1,"productType":"Bakery","expires":1480507595369,"purchaseDetails":[{"id":1,"description":"XL wedding cake","quantity":2,"value":600.0}]}
 */

/**
 * @apiDefine DataIntegrityViolation
 *
 * @apiError (Error 409) {String} code="DataIntegrityViolation"
 * @apiError (Error 409) {String} message detailed message
 *
 *  @apiErrorExample {json} Error 409 response example:
 *  HTTP/1.1 409 Conflict
 *  Content-Type: application/json;charset=UTF-8
 *  Content-Length: 72
 *
 *  {"code":"DataIntegrityViolation","message":"Purchase id already in use"}
 */

/**
 * @apiDefine DataIntegrityViolation2
 *
 * @apiError (Error 409) {String} code="DataIntegrityViolation"
 * @apiError (Error 409) {String} message detailed message
 *
 *  @apiErrorExample {json} Error 409 response example:
 *  HTTP/1.1 409 Conflict
 *  Content-Type: application/json;charset=UTF-8
 *  Content-Length: 78
 *
 *  {"code":"DataIntegrityViolation","message":"Purchase detail id already in use"}
 */

/**
 * @apiDefine InvalidPurchase
 *
 * @apiError (Error 400) {String} code="InvalidPurchase"
 * @apiError (Error 400) {String} message detailed message
 * @apiError (Error 400) {Object[]} fieldErrors list of errors
 * @apiError (Error 400) {String} fieldErrors.code code of field error
 * @apiError (Error 400) {String} fieldErrors.field field name
 * @apiError (Error 400) {String} fieldErrors.resource resource name
 * @apiError (Error 400) {String} fieldErrors.message detailed message
 *
 *  @apiErrorExample {json} Error 400 response example:
 *  HTTP/1.1 400 Bad Request
 *  Content-Type: application/json;charset=UTF-8
 *  Content-Length: 291
 *
 *  {"code":"InvalidPurchase","fieldErrors":[{"code":"","field":"productType","resource":"purchase","message":"ProductType is empty"},{"code":"","field":"purchaseDetails[0].quantity","resource":"purchase","message":"PurchaseDetail quantity is null or equals to 0"}],"message":"Invalid purchase"}
 */

/**
 * @apiDefine NotFoundException
 *
 * @apiError (Error 404) {String} code="NotFound"
 * @apiError (Error 404) {String} message detailed message
 *
 *  @apiErrorExample {json} Error 404 response example:
 *  HTTP/1.1 404 Not Found
 *  Content-Type: application/json;charset=UTF-8
 *  Content-Length: 50
 *
 *  {"code":"NotFound","message":"Purchase 1 not found"}
 */

/**
 * @apiVersion 1.0.0
 * @apiName GetValidPurchases
 * @apiGroup Purchases
 *
 * @apiDescription
 * This service allows you to get a list of all valid purchases.<br>
 *
 * @api {get} /api/purchases
 * 1. Get valid purchases
 *
 * @apiHeader {String} Accept=application/json;charset=UTF-8
 * @apiHeaderExample {json} Header-Example:
 * {
 *  "Accept": "application/json;charset=UTF-8"
 * }
 *
 * @apiExample Example usage:
 * curl 'http://localhost:8080/api/purchases' -i -H 'Accept: application/json;charset=UTF-8'
 *
 * @apiUse GetValidPurchasesSuccess200Return
 * @apiUse NoContent
 *
 *  @apiSampleRequest off
 */

/**
 * @apiVersion 1.0.0
 * @apiName SavePurchase
 * @apiGroup Purchases
 *
 * @apiDescription
 * This service allows you to save a given purchase.<br>
 *
 * @api {post} /api/purchases
 * 2. Save purchase
 *
 * @apiHeader {String} Accept=application/json;charset=UTF-8
 * @apiHeader {String} content-type=application/json;charset=UTF-8
 * @apiHeaderExample {json} Header-Example:
 * {
 *  "Accept": "application/json;charset=UTF-8",
 *  "Content-Type": "application/json;charset=UTF-8"
 * }
 *
 * @apiExample Example usage:
 * curl 'http://localhost:8080/api/purchases' -i -X POST -H 'Accept: application/json;charset=UTF-8' -H 'Content-Type: application/json;charset=UTF-8' -d '{"id":1,"productType":"Cake","expires":1480507595369,"purchaseDetails":[{"id":1,"description":"XXL wedding cake","quantity":1,"value":500.0}]}'
 *
 * @apiUse SavePurchaseSuccess200Return
 * @apiUse DataIntegrityViolation
 * @apiUse InvalidPurchase
 *
 *  @apiSampleRequest off
 */

/**
 * @apiVersion 1.0.0
 * @apiName UpdatePurchase
 * @apiGroup Purchases
 *
 * @apiDescription
 * This service allows you to update a given purchase.<br>
 *
 * @api {post} /api/purchases/:purchaseId
 * 2. Update purchase
 *
 * @@apiParam {Number} purchaseId purchase unique ID.
 *
 * @apiHeader {String} Accept=application/json;charset=UTF-8
 * @apiHeader {String} content-type=application/json;charset=UTF-8
 * @apiHeaderExample {json} Header-Example:
 * {
 *  "Accept": "application/json;charset=UTF-8",
 *  "Content-Type": "application/json;charset=UTF-8"
 * }
 *
 * @apiExample Example usage:
 * curl 'http://localhost:8080/api/purchases/1' -i -X PUT -H 'Accept: application/json;charset=UTF-8' -H 'Content-Type: application/json;charset=UTF-8' -d '{"id":1,"productType":"Bakery","purchaseDetails":[{"id":1,"description":"XL wedding cake","quantity":2,"value":600.0}]}'
 * curl 'http://localhost:8080/api/purchases/1' -i -X PATCH -H 'Accept: application/json;charset=UTF-8' -H 'Content-Type: application/json;charset=UTF-8' -d '{"id":1,"productType":"Bakery","purchaseDetails":[{"id":1,"description":"XL wedding cake","quantity":2,"value":600.0}]}'
 *
 * @apiUse UpdatePurchaseSuccess200Return
 * @apiUse DataIntegrityViolation2
 * @apiUse InvalidPurchase
 * @apiUse NotFoundException
 *
 *  @apiSampleRequest off
 */
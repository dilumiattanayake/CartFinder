'use strict';
const express = require('express');
const router = express.Router();
const menuController = require('../controllers/menu.controller');
const authMiddleware = require('../middlewares/auth');
const roleMiddleware = require('../middlewares/role');

router.get('/:vendorId',                   menuController.getByVendor);
router.post('/:vendorId',                  authMiddleware, roleMiddleware('vendor'), menuController.createOrUpdate);
router.post('/:vendorId/dishes',           authMiddleware, roleMiddleware('vendor'), menuController.addDish);
router.patch('/:vendorId/dishes/:dishId',  authMiddleware, roleMiddleware('vendor'), menuController.updateDish);
router.delete('/:vendorId/dishes/:dishId', authMiddleware, roleMiddleware('vendor'), menuController.deleteDish);

module.exports = router;

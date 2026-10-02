'use strict';
const express = require('express');
const router = express.Router();
const reviewController = require('../controllers/review.controller');
const authMiddleware   = require('../middlewares/auth');
const roleMiddleware   = require('../middlewares/role');

router.get('/:vendorId',        reviewController.getByVendor);
router.post('/',                authMiddleware, roleMiddleware('customer'), reviewController.create);
router.delete('/:reviewId',     authMiddleware, roleMiddleware('admin'), reviewController.remove);

module.exports = router;

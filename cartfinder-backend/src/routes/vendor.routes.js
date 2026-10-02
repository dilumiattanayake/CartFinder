'use strict';
const express = require('express');
const router = express.Router();
const vendorController = require('../controllers/vendor.controller');
const authMiddleware  = require('../middlewares/auth');
const roleMiddleware  = require('../middlewares/role');

// Public — nearby vendors geo-query
router.get('/nearby', vendorController.getNearby);

// Public — vendor public profile
router.get('/:id', vendorController.getById);

// Vendor — create own profile
router.post('/',    authMiddleware, roleMiddleware('vendor'), vendorController.create);

// Vendor — update own profile
router.put('/:id',  authMiddleware, roleMiddleware('vendor', 'admin'), vendorController.update);

// Vendor — toggle live GPS broadcast
router.patch('/:id/broadcast', authMiddleware, roleMiddleware('vendor'), vendorController.toggleBroadcast);

// Admin / PHI — update approval status
router.patch('/:id/status', authMiddleware, roleMiddleware('admin', 'phi'), vendorController.updateStatus);

module.exports = router;

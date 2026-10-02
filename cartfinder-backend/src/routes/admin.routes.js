'use strict';
const express = require('express');
const router = express.Router();
const adminController = require('../controllers/admin.controller');
const authMiddleware  = require('../middlewares/auth');
const roleMiddleware  = require('../middlewares/role');

const adminOnly = [authMiddleware, roleMiddleware('admin')];

router.get('/stats',           ...adminOnly, adminController.globalStats);
router.get('/users',           ...adminOnly, adminController.listUsers);
router.patch('/users/:id/status', ...adminOnly, adminController.updateUserStatus);
router.get('/flagged-vendors', ...adminOnly, adminController.flaggedVendors);

module.exports = router;

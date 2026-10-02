'use strict';
const express = require('express');
const router = express.Router();
const complaintController = require('../controllers/complaint.controller');
const authMiddleware      = require('../middlewares/auth');
const roleMiddleware      = require('../middlewares/role');

router.post('/',          authMiddleware, roleMiddleware('customer', 'vendor'), complaintController.file);
router.get('/',           authMiddleware, roleMiddleware('phi', 'admin'), complaintController.list);
router.get('/:id',        authMiddleware, roleMiddleware('phi', 'admin'), complaintController.getById);
router.patch('/:id/resolve', authMiddleware, roleMiddleware('phi', 'admin'), complaintController.resolve);

module.exports = router;

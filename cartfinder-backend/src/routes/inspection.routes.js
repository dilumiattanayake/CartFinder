'use strict';
const express = require('express');
const router = express.Router();
const inspectionController = require('../controllers/inspection.controller');
const authMiddleware       = require('../middlewares/auth');
const roleMiddleware       = require('../middlewares/role');

// PHI badge issuance & retrieval
router.get('/badge/:vendorId',   inspectionController.getBadge);
router.post('/badge',            authMiddleware, roleMiddleware('phi', 'admin'), inspectionController.issueBadge);

// Audit logs
router.post('/audit',            authMiddleware, roleMiddleware('phi', 'admin'), inspectionController.logAudit);
router.get('/audit/:vendorId',   authMiddleware, roleMiddleware('phi', 'admin'), inspectionController.getAuditHistory);

// QR verification
router.post('/verify-qr',        authMiddleware, inspectionController.verifyQR);

module.exports = router;

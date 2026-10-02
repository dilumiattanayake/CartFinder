'use strict';
const express = require('express');
const router = express.Router();
const authController = require('../controllers/auth.controller');
const authMiddleware = require('../middlewares/auth');

// POST /api/v1/auth/register  — create MongoDB user after Firebase sign-up
// NOTE: Does NOT use authMiddleware because we're creating the MongoDB record
// Instead, we verify the Firebase token directly in the controller
router.post('/register', authController.register);

// GET  /api/v1/auth/me        — return current user profile
router.get('/me', authMiddleware, authController.me);

module.exports = router;

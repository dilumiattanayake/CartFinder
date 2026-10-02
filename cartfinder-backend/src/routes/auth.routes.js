'use strict';
const express = require('express');
const router = express.Router();
const authController = require('../controllers/auth.controller');
const authMiddleware = require('../middlewares/auth');

// POST /api/v1/auth/register  — create MongoDB user after Firebase sign-up
router.post('/register', authMiddleware, authController.register);

// GET  /api/v1/auth/me        — return current user profile
router.get('/me', authMiddleware, authController.me);

module.exports = router;

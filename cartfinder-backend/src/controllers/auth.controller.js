'use strict';
const asyncHandler = require('../utils/asyncHandler');
const { successResponse, ApiError } = require('../utils/apiResponse');
const User = require('../models/User');
const schemas = require('../utils/validators');

/**
 * POST /api/v1/auth/register
 * Creates a new MongoDB User document after the client has
 * completed Firebase sign-up and obtained a valid ID token.
 */
exports.register = asyncHandler(async (req, res, next) => {
  const { error, value } = schemas.registerUser.validate(req.body);
  if (error) return next(new ApiError(422, error.message));

  const { firebaseUid, email, role, displayName } = value;

  // Guard: ensure token uid matches request body uid
  if (req.user.firebaseUser.uid !== firebaseUid) {
    return next(new ApiError(403, 'Firebase UID mismatch'));
  }

  const existing = await User.findOne({ firebaseUid });
  if (existing) return res.status(200).json(successResponse(existing, 'User already registered'));

  const user = await User.create({ firebaseUid, email, role, displayName });
  return res.status(201).json(successResponse(user, 'User registered successfully'));
});

/**
 * GET /api/v1/auth/me
 * Returns the current user's MongoDB profile.
 */
exports.me = asyncHandler(async (req, res) => {
  return res.json(successResponse(req.user.dbUser));
});

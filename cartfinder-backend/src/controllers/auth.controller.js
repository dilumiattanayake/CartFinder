'use strict';
const asyncHandler = require('../utils/asyncHandler');
const { successResponse, ApiError } = require('../utils/apiResponse');
const User = require('../models/User');
const schemas = require('../utils/validators');
const admin = require('../config/firebase');

/**
 * POST /api/v1/auth/register
 * Creates a new MongoDB User document after the client has
 * completed Firebase sign-up and obtained a valid ID token.
 * 
 * This endpoint does NOT use the auth middleware because it's meant to
 * CREATE the MongoDB user record. Instead, it verifies the Firebase token
 * directly in this controller.
 */
exports.register = asyncHandler(async (req, res, next) => {
  const { error, value } = schemas.registerUser.validate(req.body);
  if (error) return next(new ApiError(422, error.message));

  const { firebaseUid, email, role, displayName } = value;
  
  console.log('[auth.controller] Register request:', { firebaseUid, email, role, displayName });

  // Verify the Firebase token from the Authorization header
  const authHeader = req.headers.authorization;
  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    console.error('[auth.controller] No valid token provided');
    return next(new ApiError(401, 'No authentication token provided'));
  }

  const idToken = authHeader.split('Bearer ')[1];
  
  try {
    console.log('[auth.controller] Verifying Firebase token...');
    const decodedToken = await admin.auth().verifyIdToken(idToken);
    console.log('[auth.controller] Token verified for uid:', decodedToken.uid);
    
    // Guard: ensure token uid matches request body uid
    if (decodedToken.uid !== firebaseUid) {
      console.error('[auth.controller] UID mismatch:', { tokenUid: decodedToken.uid, bodyUid: firebaseUid });
      return next(new ApiError(403, 'Firebase UID mismatch'));
    }

    const existing = await User.findOne({ firebaseUid });
    if (existing) {
      console.log('[auth.controller] User already exists:', existing._id);
      return res.status(200).json(successResponse(existing, 'User already registered'));
    }

    console.log('[auth.controller] Creating new user...');
    const user = await User.create({ firebaseUid, email, role, displayName });
    console.log('[auth.controller] User created successfully:', user._id);
    return res.status(201).json(successResponse(user, 'User registered successfully'));
  } catch (err) {
    console.error('[auth.controller] Token verification failed:', err.message);
    if (err.code === 'auth/id-token-expired') {
      return next(new ApiError(401, 'Token expired. Please re-authenticate.'));
    }
    return next(new ApiError(401, `Invalid authentication token: ${err.message}`));
  }
});


/**
 * GET /api/v1/auth/me
 * Returns the current user's MongoDB profile.
 */
exports.me = asyncHandler(async (req, res) => {
  return res.json(successResponse(req.user.dbUser));
});

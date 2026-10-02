'use strict';
const admin = require('../config/firebase');
const User = require('../models/User');
const { ApiError } = require('../utils/apiResponse');

/**
 * Firebase Admin Token Verifier & User Context Injector
 *
 * Pipeline:
 *  1. Extract Bearer token from the Authorization header.
 *  2. Verify the ID token with Firebase Admin SDK (checks signature,
 *     expiry, issuer, and audience).
 *  3. Look up the corresponding MongoDB User document by firebaseUid.
 *  4. Attach { firebaseUser, dbUser } to req.user for downstream use.
 *
 * Usage:
 *   router.get('/protected', authMiddleware, controller);
 */
const authMiddleware = async (req, res, next) => {
  const authHeader = req.headers.authorization;
  
  console.log('[auth.middleware] Incoming request:', req.method, req.path);
  console.log('[auth.middleware] Authorization header present:', !!authHeader);

  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    console.error('[auth.middleware] No valid token provided');
    return next(new ApiError(401, 'No authentication token provided'));
  }

  const idToken = authHeader.split('Bearer ')[1];

  try {
    // Step 1: Cryptographically verify the Firebase ID token
    console.log('[auth.middleware] Verifying token...');
    const decodedToken = await admin.auth().verifyIdToken(idToken);
    console.log('[auth.middleware] Token verified for uid:', decodedToken.uid);

    // Step 2: Resolve the local MongoDB user record
    console.log('[auth.middleware] Looking up MongoDB user:', decodedToken.uid);
    const dbUser = await User.findOne({ firebaseUid: decodedToken.uid }).lean();
    if (!dbUser) {
      console.error('[auth.middleware] User not found in MongoDB for uid:', decodedToken.uid);
      return next(
        new ApiError(404, 'User record not found. Please complete registration.'),
      );
    }

    console.log('[auth.middleware] User found:', dbUser._id, '- Role:', dbUser.role);

    // Step 3: Inject full context into request
    req.user = {
      firebaseUser: decodedToken, // { uid, email, email_verified, name, ... }
      dbUser,                      // { _id, role, firebaseUid, ... }
    };

    next();
  } catch (err) {
    console.error('[auth.middleware] Token verification failed:', err.message);
    if (err.code === 'auth/id-token-expired') {
      return next(new ApiError(401, 'Token expired. Please re-authenticate.'));
    }
    return next(new ApiError(401, `Invalid authentication token: ${err.message}`));
  }
};

module.exports = authMiddleware;

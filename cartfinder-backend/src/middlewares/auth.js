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

  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    return next(new ApiError(401, 'No authentication token provided'));
  }

  const idToken = authHeader.split('Bearer ')[1];

  try {
    // Step 1: Cryptographically verify the Firebase ID token
    const decodedToken = await admin.auth().verifyIdToken(idToken);

    // Step 2: Resolve the local MongoDB user record
    const dbUser = await User.findOne({ firebaseUid: decodedToken.uid }).lean();
    if (!dbUser) {
      return next(
        new ApiError(404, 'User record not found. Please complete registration.'),
      );
    }

    // Step 3: Inject full context into request
    req.user = {
      firebaseUser: decodedToken, // { uid, email, email_verified, name, ... }
      dbUser,                      // { _id, role, firebaseUid, ... }
    };

    next();
  } catch (err) {
    if (err.code === 'auth/id-token-expired') {
      return next(new ApiError(401, 'Token expired. Please re-authenticate.'));
    }
    return next(new ApiError(401, `Invalid authentication token: ${err.message}`));
  }
};

module.exports = authMiddleware;

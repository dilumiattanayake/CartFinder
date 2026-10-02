'use strict';
const admin = require('firebase-admin');
const path = require('path');

/**
 * Initialises the Firebase Admin SDK once.
 * Credentials are loaded from the service account JSON file
 * whose path is specified in FIREBASE_SERVICE_ACCOUNT_PATH.
 *
 * NOTE: Never commit serviceAccountKey.json to version control.
 */
if (!admin.apps.length) {
  const serviceAccountPath = path.resolve(
    process.cwd(),
    process.env.FIREBASE_SERVICE_ACCOUNT_PATH || './serviceAccountKey.json',
  );

  admin.initializeApp({
    credential: admin.credential.cert(serviceAccountPath),
  });
}

module.exports = admin;

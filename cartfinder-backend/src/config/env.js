'use strict';
/**
 * Centralised environment variable loader & validator.
 * Import this early (before any other src/ file) to fail-fast
 * on missing required variables.
 */
const required = [
  'MONGODB_URI',
  'FIREBASE_SERVICE_ACCOUNT_PATH',
];

required.forEach((key) => {
  if (!process.env[key]) {
    throw new Error(`Missing required environment variable: ${key}`);
  }
});

module.exports = {
  port:              Number(process.env.PORT) || 5000,
  nodeEnv:           process.env.NODE_ENV || 'development',
  mongodbUri:        process.env.MONGODB_URI,
  allowedOrigins:    process.env.ALLOWED_ORIGINS?.split(',') || ['*'],
  qrSecret:          process.env.QR_SECRET || 'changeme',
};

'use strict';
const logger = require('../utils/logger');
const { errorResponse } = require('../utils/apiResponse');

/**
 * Global Error Handler Middleware
 *
 * Must be the LAST middleware registered in server.js.
 * Catches all errors forwarded via next(err) and returns
 * a standardised JSON error envelope.
 *
 * Handles:
 *  - ApiError instances (custom, operational errors)
 *  - Mongoose ValidationError / CastError
 *  - Mongoose duplicate key error (code 11000)
 *  - Generic / unexpected errors
 */
// eslint-disable-next-line no-unused-vars
const errorMiddleware = (err, req, res, next) => {
  let statusCode = err.statusCode || 500;
  let message = err.message || 'Internal Server Error';
  let errors = err.errors || [];

  // Mongoose validation error
  if (err.name === 'ValidationError') {
    statusCode = 422;
    message = 'Validation failed';
    errors = Object.values(err.errors).map((e) => ({
      field: e.path,
      message: e.message,
    }));
  }

  // Mongoose bad ObjectId
  if (err.name === 'CastError') {
    statusCode = 400;
    message = `Invalid value for field: ${err.path}`;
  }

  // MongoDB duplicate key
  if (err.code === 11000) {
    statusCode = 409;
    const field = Object.keys(err.keyValue || {})[0] || 'field';
    message = `Duplicate value: ${field} already exists`;
  }

  // Log 5xx errors
  if (statusCode >= 500) {
    logger.error(`[${req.method}] ${req.originalUrl} — ${err.stack || err.message}`);
  }

  return res.status(statusCode).json(
    errorResponse(message, statusCode, errors),
  );
};

module.exports = errorMiddleware;

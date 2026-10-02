'use strict';

/**
 * Standardised API response helpers.
 * All endpoints return one of these two envelope shapes:
 *
 * Success: { success: true,  data: T,   message: string }
 * Error:   { success: false, error: string, statusCode: number, errors: [] }
 */

const successResponse = (data = null, message = 'Success') => ({
  success: true,
  message,
  data,
});

const errorResponse = (message = 'Error', statusCode = 500, errors = []) => ({
  success: false,
  message,
  statusCode,
  errors,
});

/**
 * Operational (expected) API error.
 * Throw or pass to next() inside controllers.
 */
class ApiError extends Error {
  constructor(statusCode, message, errors = []) {
    super(message);
    this.statusCode = statusCode;
    this.errors = errors;
    this.isOperational = true;
    Error.captureStackTrace(this, this.constructor);
  }
}

module.exports = { successResponse, errorResponse, ApiError };

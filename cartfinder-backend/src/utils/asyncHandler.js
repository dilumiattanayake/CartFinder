'use strict';

/**
 * Wraps an async Express route handler so that any thrown error
 * is automatically forwarded to next() — eliminating the need for
 * try/catch boilerplate in every controller.
 *
 * Usage:
 *   router.get('/vendors', asyncHandler(vendorController.list));
 */
const asyncHandler = (fn) => (req, res, next) =>
  Promise.resolve(fn(req, res, next)).catch(next);

module.exports = asyncHandler;

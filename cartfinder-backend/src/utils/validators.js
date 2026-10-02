'use strict';
const Joi = require('joi');

/**
 * Reusable Joi validation schemas for request body / query params.
 * Usage in controller:
 *   const { error } = schemas.registerUser.validate(req.body);
 *   if (error) return next(new ApiError(422, error.message));
 */

const schemas = {
  registerUser: Joi.object({
    firebaseUid: Joi.string().required(),
    email:       Joi.string().email().required(),
    role:        Joi.string().valid('customer', 'vendor', 'phi').required(),
    displayName: Joi.string().min(2).max(60).allow(null, '').optional(),
  }),

  updateVendorLocation: Joi.object({
    latitude:  Joi.number().min(-90).max(90).required(),
    longitude: Joi.number().min(-180).max(180).required(),
    label:     Joi.string().max(200),
  }),

  nearbyVendors: Joi.object({
    lat:     Joi.number().min(-90).max(90).required(),
    lng:     Joi.number().min(-180).max(180).required(),
    radius:  Joi.number().min(100).max(50000).default(2000), // metres
  }),

  submitReview: Joi.object({
    vendorId:    Joi.string().required(),
    rating:      Joi.number().min(1).max(5).required(),
    comment:     Joi.string().max(1000),
    hygieneCheck: Joi.object({
      cleanCookingArea: Joi.boolean(),
      propFoodStorage:  Joi.boolean(),
      gloveUse:         Joi.boolean(),
      cleanUtensils:    Joi.boolean(),
    }),
  }),

  fileComplaint: Joi.object({
    vendorId:    Joi.string().required(),
    description: Joi.string().min(20).max(2000).required(),
    priority:    Joi.string().valid('low', 'medium', 'high', 'critical').default('medium'),
    category:    Joi.string().valid(
      'hygiene', 'food_quality', 'behaviour', 'license', 'other',
    ).required(),
  }),

  issueHygieneBadge: Joi.object({
    vendorId:       Joi.string().required(),
    grade:          Joi.string().valid('A', 'B', 'C', 'D').required(),
    notes:          Joi.string().max(2000),
    nextInspection: Joi.date().iso(),
  }),
};

module.exports = schemas;

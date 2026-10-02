'use strict';
const mongoose = require('mongoose');

/**
 * Quick-check hygiene checklist embedded in reviews.
 * Customers answer these simple boolean questions at point of visit.
 */
const HygieneCheckSchema = new mongoose.Schema(
  {
    cleanCookingArea: { type: Boolean },
    propFoodStorage:  { type: Boolean },
    gloveUse:         { type: Boolean },
    cleanUtensils:    { type: Boolean },
  },
  { _id: false },
);

const ReviewSchema = new mongoose.Schema(
  {
    vendorId: {
      type:     mongoose.Schema.Types.ObjectId,
      ref:      'Vendor',
      required: true,
      index:    true,
    },
    reviewerUid: { type: String, required: true },  // Firebase UID
    reviewerName: { type: String },

    rating:      { type: Number, required: true, min: 1, max: 5 },
    comment:     { type: String, maxlength: 1000 },
    hygieneCheck: HygieneCheckSchema,

    // Location verification — optional GPS check at review time
    verifiedLocation: { type: Boolean, default: false },
    reviewLocation: {
      type:        { type: String, enum: ['Point'] },
      coordinates: { type: [Number] },
    },

    isHidden:   { type: Boolean, default: false }, // admin moderation
    isEdited:   { type: Boolean, default: false },
  },
  { timestamps: true },
);

ReviewSchema.index({ vendorId: 1, createdAt: -1 });
ReviewSchema.index({ reviewerUid: 1 });
// One review per user per vendor constraint
ReviewSchema.index({ vendorId: 1, reviewerUid: 1 }, { unique: true });

module.exports = mongoose.model('Review', ReviewSchema);

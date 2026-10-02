'use strict';
const mongoose = require('mongoose');

/** Operating Hours sub-schema (per day of week) */
const OperatingHoursSchema = new mongoose.Schema(
  {
    day:       { type: String, enum: ['Mon','Tue','Wed','Thu','Fri','Sat','Sun'], required: true },
    openTime:  { type: String, required: true },  // "08:30"
    closeTime: { type: String, required: true },  // "18:00"
    isClosed:  { type: Boolean, default: false },
  },
  { _id: false },
);

/**
 * Vendor / Cart Schema
 *
 * Location stored as GeoJSON Point for MongoDB 2dsphere geospatial queries.
 * Use $near, $geoWithin, and $geoNear aggregation stage to find nearby carts.
 *
 * IMPORTANT: GeoJSON coordinate order is [longitude, latitude] — the reverse
 * of the conventional (lat, lng) pair used by most mapping libraries.
 */
const VendorSchema = new mongoose.Schema(
  {
    ownerUid: { type: String, required: true, unique: true, index: true },

    stallName:    { type: String, required: true, trim: true },
    description:  { type: String, maxlength: 500 },
    category:     { type: String, enum: ['Food','Beverages','Snacks','Mixed'], default: 'Food' },
    profileImage: { type: String },       // CDN / storage URL
    phoneNumber:  { type: String },
    licenseNumber: { type: String },
    licenseVerified: { type: Boolean, default: false },

    // ── GeoJSON Location ────────────────────────────────────────────────
    location: {
      type: {
        type:     String,
        enum:     ['Point'],
        required: true,
      },
      coordinates: {
        type:     [Number],   // [longitude, latitude]
        required: true,
        validate: {
          validator: ([lng, lat]) =>
            lng >= -180 && lng <= 180 && lat >= -90 && lat <= 90,
          message: 'Invalid GeoJSON coordinates: longitude must be ±180, latitude ±90',
        },
      },
    },

    // ── Live Broadcast ──────────────────────────────────────────────────
    isLive:        { type: Boolean, default: false },
    lastBroadcast: { type: Date },
    locationLabel: { type: String, maxlength: 200 },

    // ── Schedule ────────────────────────────────────────────────────────
    operatingHours: [OperatingHoursSchema],

    // ── Ratings (denormalised aggregate for fast reads) ──────────────────
    averageRating: { type: Number, default: 0, min: 0, max: 5 },
    reviewCount:   { type: Number, default: 0 },

    // ── Hygiene ─────────────────────────────────────────────────────────
    currentHygieneGrade: {
      type:    String,
      enum:    ['A','B','C','D','Unrated'],
      default: 'Unrated',
    },
    isFlagged:  { type: Boolean, default: false },
    flagCount:  { type: Number,  default: 0 },

    // ── Approval Workflow ───────────────────────────────────────────────
    approvalStatus: {
      type:    String,
      enum:    ['pending','approved','suspended','rejected'],
      default: 'pending',
    },

    isActive: { type: Boolean, default: true },
  },
  { timestamps: true, toJSON: { virtuals: true }, toObject: { virtuals: true } },
);

// ── Geospatial Index (REQUIRED for $near / $geoWithin queries) ────────────
VendorSchema.index({ location: '2dsphere' });

// ── Compound Indexes for common query patterns ─────────────────────────────
VendorSchema.index({ isLive: 1, isActive: 1, approvalStatus: 1 });
VendorSchema.index({ isFlagged: 1, approvalStatus: 1 });
VendorSchema.index({ currentHygieneGrade: 1 });

/** Returns coordinates in conventional { lat, lng } form (virtual). */
VendorSchema.virtual('coords').get(function () {
  const [lng, lat] = this.location.coordinates;
  return { lat, lng };
});

module.exports = mongoose.model('Vendor', VendorSchema);

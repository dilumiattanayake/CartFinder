'use strict';
const mongoose = require('mongoose');

/**
 * Base User Schema — single collection, role-discriminated.
 * Firebase UID is the canonical cross-system identifier.
 * Role determines which additional fields / sub-schemas apply.
 */
const UserSchema = new mongoose.Schema(
  {
    firebaseUid: { type: String, required: true, unique: true, index: true },
    email:       { type: String, required: true, lowercase: true, trim: true },
    displayName: { type: String, trim: true },
    photoURL:    { type: String },
    phoneNumber: { type: String },

    role: {
      type:     String,
      enum:     ['customer', 'vendor', 'phi', 'admin'],
      required: true,
      index:    true,
    },

    isActive:   { type: Boolean, default: true },
    lastSeenAt: { type: Date },

    // Push notification token (FCM)
    fcmToken: { type: String },

    // Vendor-specific: reference to Vendor document
    vendorProfile: { type: mongoose.Schema.Types.ObjectId, ref: 'Vendor' },

    // PHI-specific: badge number, jurisdiction
    phiDetails: {
      badgeNumber:  { type: String },
      jurisdiction: { type: String },
      department:   { type: String },
    },
  },
  { timestamps: true },
);

UserSchema.index({ email: 1 });
UserSchema.index({ role: 1, isActive: 1 });

module.exports = mongoose.model('User', UserSchema);

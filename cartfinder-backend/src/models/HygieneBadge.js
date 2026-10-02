'use strict';
const mongoose = require('mongoose');

/** Single PHI inspection entry */
const InspectionEntrySchema = new mongoose.Schema(
  {
    phiUid:         { type: String, required: true },
    phiName:        { type: String },
    inspectionDate: { type: Date, required: true, default: Date.now },
    grade:          { type: String, enum: ['A','B','C','D'], required: true },
    notes:          { type: String, maxlength: 3000 },
    violations:     [{ type: String }],
    followUpRequired: { type: Boolean, default: false },
  },
  { timestamps: true },
);

/**
 * HygieneBadge — PHI-issued official sanitation rating for a vendor.
 * One document per vendor; inspection history is an embedded array.
 */
const HygieneBadgeSchema = new mongoose.Schema(
  {
    vendorId: {
      type:     mongoose.Schema.Types.ObjectId,
      ref:      'Vendor',
      required: true,
      unique:   true,
    },

    currentGrade:       { type: String, enum: ['A','B','C','D','Unrated'], default: 'Unrated' },
    lastInspectionDate: { type: Date },
    nextInspectionDate: { type: Date },
    certifyingPhiUid:   { type: String },

    // QR badge payload — encoded as JWT or signed JSON string
    qrPayload:  { type: String },
    qrIssuedAt: { type: Date },

    inspectionHistory: [InspectionEntrySchema],

    isRevoked: { type: Boolean, default: false },
  },
  { timestamps: true },
);

HygieneBadgeSchema.index({ currentGrade: 1 });

module.exports = mongoose.model('HygieneBadge', HygieneBadgeSchema);

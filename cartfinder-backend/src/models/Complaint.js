'use strict';
const mongoose = require('mongoose');

const ComplaintSchema = new mongoose.Schema(
  {
    vendorId: {
      type:     mongoose.Schema.Types.ObjectId,
      ref:      'Vendor',
      required: true,
      index:    true,
    },
    reporterUid:  { type: String, required: true },
    reporterName: { type: String },

    category: {
      type:  String,
      enum:  ['hygiene','food_quality','behaviour','license','other'],
      required: true,
    },
    priority: {
      type:    String,
      enum:    ['low','medium','high','critical'],
      default: 'medium',
      index:   true,
    },
    description: { type: String, required: true, minlength: 20, maxlength: 2000 },
    attachments:  [{ type: String }],  // image URLs

    status: {
      type:    String,
      enum:    ['open','under_review','resolved','dismissed'],
      default: 'open',
      index:   true,
    },
    resolutionNote: { type: String },
    resolvedByUid:  { type: String },
    resolvedAt:     { type: Date },

    // Auto-flag the vendor after N complaints
    triggeredAutoFlag: { type: Boolean, default: false },
  },
  { timestamps: true },
);

ComplaintSchema.index({ vendorId: 1, status: 1 });
ComplaintSchema.index({ reporterUid: 1 });
ComplaintSchema.index({ priority: 1, status: 1, createdAt: -1 });

module.exports = mongoose.model('Complaint', ComplaintSchema);

'use strict';
const mongoose = require('mongoose');

/**
 * PHI Spot Audit Log.
 * Stores every field audit / dossier entry performed by a PHI officer.
 */
const AuditLogSchema = new mongoose.Schema(
  {
    vendorId: {
      type:     mongoose.Schema.Types.ObjectId,
      ref:      'Vendor',
      required: true,
      index:    true,
    },
    phiUid:  { type: String, required: true },
    phiName: { type: String },

    auditType: {
      type: String,
      enum: ['routine','complaint_followup','spot_check','license_renewal'],
      required: true,
    },

    sector:   { type: String },  // Geographic sector / zone
    findings: { type: String, maxlength: 5000 },
    actions:  [{ type: String }],  // e.g. ['Issued warning', 'Sealed stall']

    gradeIssued: { type: String, enum: ['A','B','C','D'] },

    auditedAt: { type: Date, default: Date.now },
    followUpDate: { type: Date },
    isClosed:  { type: Boolean, default: false },
  },
  { timestamps: true },
);

AuditLogSchema.index({ vendorId: 1, auditedAt: -1 });
AuditLogSchema.index({ phiUid: 1 });
AuditLogSchema.index({ sector: 1, auditedAt: -1 });

module.exports = mongoose.model('AuditLog', AuditLogSchema);

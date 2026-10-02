'use strict';
const asyncHandler = require('../utils/asyncHandler');
const { successResponse, ApiError } = require('../utils/apiResponse');
const HygieneBadge = require('../models/HygieneBadge');
const AuditLog     = require('../models/AuditLog');
const Vendor       = require('../models/Vendor');
const qrService    = require('../services/qrService');
const schemas      = require('../utils/validators');

exports.getBadge = asyncHandler(async (req, res) => {
  const badge = await HygieneBadge.findOne({ vendorId: req.params.vendorId });
  return res.json(successResponse(badge));
});

exports.issueBadge = asyncHandler(async (req, res, next) => {
  const { error, value } = schemas.issueHygieneBadge.validate(req.body);
  if (error) return next(new ApiError(422, error.message));

  const { vendorId, grade, notes, nextInspection } = value;
  const phiUid = req.user.firebaseUser.uid;

  const qrPayload = await qrService.generateBadgeQR({ vendorId, grade, phiUid });

  const badge = await HygieneBadge.findOneAndUpdate(
    { vendorId },
    {
      currentGrade:       grade,
      lastInspectionDate: new Date(),
      nextInspectionDate: nextInspection,
      certifyingPhiUid:   phiUid,
      qrPayload,
      qrIssuedAt:         new Date(),
      $push: {
        inspectionHistory: {
          phiUid, grade, notes, inspectionDate: new Date(),
        },
      },
    },
    { new: true, upsert: true },
  );

  // Sync grade to vendor document
  await Vendor.findByIdAndUpdate(vendorId, { currentHygieneGrade: grade });

  return res.status(201).json(successResponse(badge, 'Hygiene badge issued'));
});

exports.logAudit = asyncHandler(async (req, res) => {
  const phiUid = req.user.firebaseUser.uid;
  const log = await AuditLog.create({ ...req.body, phiUid });
  return res.status(201).json(successResponse(log, 'Audit logged'));
});

exports.getAuditHistory = asyncHandler(async (req, res) => {
  const logs = await AuditLog.find({ vendorId: req.params.vendorId }).sort({ auditedAt: -1 });
  return res.json(successResponse(logs));
});

exports.verifyQR = asyncHandler(async (req, res, next) => {
  const { qrPayload } = req.body;
  const result = await qrService.verifyBadgeQR(qrPayload);
  if (!result.valid) return next(new ApiError(400, 'Invalid or expired QR code'));
  return res.json(successResponse(result.data, 'QR verified'));
});

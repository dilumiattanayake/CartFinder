'use strict';
const asyncHandler = require('../utils/asyncHandler');
const { successResponse, ApiError } = require('../utils/apiResponse');
const Complaint = require('../models/Complaint');
const Vendor    = require('../models/Vendor');
const schemas   = require('../utils/validators');

const AUTO_FLAG_THRESHOLD = 3; // Number of open complaints to auto-flag a vendor

exports.file = asyncHandler(async (req, res, next) => {
  const { error, value } = schemas.fileComplaint.validate(req.body);
  if (error) return next(new ApiError(422, error.message));

  const complaint = await Complaint.create({
    ...value,
    reporterUid: req.user.firebaseUser.uid,
  });

  // Auto-flag vendor if threshold exceeded
  const openCount = await Complaint.countDocuments({
    vendorId: value.vendorId,
    status: { $in: ['open', 'under_review'] },
  });
  if (openCount >= AUTO_FLAG_THRESHOLD) {
    await Vendor.findByIdAndUpdate(value.vendorId, {
      $set: { isFlagged: true },
      $inc: { flagCount: 1 },
    });
    complaint.triggeredAutoFlag = true;
    await complaint.save();
  }

  return res.status(201).json(successResponse(complaint, 'Complaint filed'));
});

exports.list = asyncHandler(async (req, res) => {
  const { status, priority, page = 1, limit = 20 } = req.query;
  const filter = {};
  if (status)   filter.status   = status;
  if (priority) filter.priority = priority;

  const complaints = await Complaint.find(filter)
    .sort({ priority: -1, createdAt: -1 })
    .skip((page - 1) * limit)
    .limit(Number(limit));

  return res.json(successResponse(complaints));
});

exports.getById = asyncHandler(async (req, res, next) => {
  const complaint = await Complaint.findById(req.params.id);
  if (!complaint) return next(new ApiError(404, 'Complaint not found'));
  return res.json(successResponse(complaint));
});

exports.resolve = asyncHandler(async (req, res, next) => {
  const { resolutionNote, status } = req.body;
  const complaint = await Complaint.findByIdAndUpdate(
    req.params.id,
    { status, resolutionNote, resolvedByUid: req.user.firebaseUser.uid, resolvedAt: new Date() },
    { new: true },
  );
  if (!complaint) return next(new ApiError(404, 'Complaint not found'));
  return res.json(successResponse(complaint, 'Complaint resolved'));
});

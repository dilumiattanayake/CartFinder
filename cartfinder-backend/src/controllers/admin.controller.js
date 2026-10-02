'use strict';

const asyncHandler = require('../utils/asyncHandler');
const { successResponse, ApiError } = require('../utils/apiResponse');
const User = require('../models/User');
const Vendor = require('../models/Vendor');
const Complaint = require('../models/Complaint');

const parsePagination = (query) => {
  const page = Math.max(Number.parseInt(query.page, 10) || 1, 1);
  const limit = Math.min(Math.max(Number.parseInt(query.limit, 10) || 20, 1), 100);
  return { page, limit, skip: (page - 1) * limit };
};

exports.globalStats = asyncHandler(async (_req, res) => {
  const [users, vendors, complaints, flaggedVendors] = await Promise.all([
    User.countDocuments(),
    Vendor.countDocuments(),
    Complaint.countDocuments(),
    Vendor.countDocuments({ isFlagged: true }),
  ]);

  return res.json(successResponse({ users, vendors, complaints, flaggedVendors }));
});

exports.listUsers = asyncHandler(async (req, res) => {
  const { page, limit, skip } = parsePagination(req.query);
  const filter = {};
  if (req.query.role) filter.role = req.query.role;
  if (req.query.isActive !== undefined) filter.isActive = req.query.isActive === 'true';

  const [users, total] = await Promise.all([
    User.find(filter)
      .select('-fcmToken')
      .sort({ createdAt: -1 })
      .skip(skip)
      .limit(limit)
      .lean(),
    User.countDocuments(filter),
  ]);

  return res.json(successResponse({
    users,
    pagination: { page, limit, total, pages: Math.ceil(total / limit) },
  }));
});

exports.updateUserStatus = asyncHandler(async (req, res, next) => {
  const { isActive } = req.body;
  if (typeof isActive !== 'boolean') {
    return next(new ApiError(400, 'isActive must be a boolean'));
  }

  const user = await User.findByIdAndUpdate(
    req.params.id,
    { isActive },
    { new: true, runValidators: true },
  ).select('-fcmToken');

  if (!user) return next(new ApiError(404, 'User not found'));
  return res.json(successResponse(user, `User ${isActive ? 'activated' : 'deactivated'}`));
});

exports.flaggedVendors = asyncHandler(async (req, res) => {
  const { page, limit, skip } = parsePagination(req.query);
  const filter = { isFlagged: true };

  const [vendors, total] = await Promise.all([
    Vendor.find(filter)
      .sort({ flagCount: -1, updatedAt: -1 })
      .skip(skip)
      .limit(limit)
      .lean(),
    Vendor.countDocuments(filter),
  ]);

  return res.json(successResponse({
    vendors,
    pagination: { page, limit, total, pages: Math.ceil(total / limit) },
  }));
});
